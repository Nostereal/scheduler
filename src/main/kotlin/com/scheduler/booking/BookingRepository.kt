package com.scheduler.booking

import com.groupstp.isdayoff.IsDayOff
import com.scheduler.booking.models.BookingsForDateResponse
import com.scheduler.booking.models.BookingsForTimeWindow
import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.SystemConfigDao
import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.db.tables.BookingEntity
import com.scheduler.profile.models.Booking
import com.scheduler.profile.toBookingModel
import com.scheduler.shared.models.ErrorWithMessage
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.isDayWorking
import com.scheduler.utils.moscowZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*

class BookingRepository(
    private val bookingsDao: BookingsDao,
    private val systemConfigDao: SystemConfigDao,
    private val isDayOff: IsDayOff,
) {

    suspend fun deleteBooking(id: UUID): TypedResult<ErrorWithMessage> {
        val success = bookingsDao.deleteBooking(id)
        return if (success) {
            TypedResult.Ok(null)
        } else {
            TypedResult.InternalError("No booking with id = $id was found")
        }
    }

    suspend fun createBooking(userId: Long, startDate: ZonedDateTime): TypedResult<Booking> {
        if (isDayOff.isDayWorking(startDate)) {
            return TypedResult.BadRequest("This day is non-working")
        }

//        bookingsDao.allBookingsByDate()

        val dbBooking = bookingsDao.insertBooking(
            BookingDbModel(
                date = startDate.toLocalDate(),
                time = startDate.toLocalTime(),
                ownerId = userId
            )
        )
        return TypedResult.Ok(dbBooking.toBookingModel())
    }

    suspend fun getBookingsByDate(date: ZonedDateTime): TypedResult<BookingsForDateResponse> = coroutineScope {
        val bookingsDef = async(Dispatchers.IO) { bookingsDao.allBookingsByDate(date.toLocalDate()) }
        val configDef = async(Dispatchers.IO) { systemConfigDao.getConfigForDate(date) }

        if (!isDayOff.isDayWorking(date)) {
            val message = "Passed day isn't working one"
            cancel(message)
            return@coroutineScope TypedResult.BadRequest(message)
        }

        val bookings = bookingsDef.await()
        val config = configDef.await()

        val slotsPerSession = config.slotsPerSession
        val sessionLengthSec = config.sessionSeconds.toLong()

        var sessionTime = config.workingHoursStart..config.workingHoursStart.plusSeconds(sessionLengthSec)
        val map = sortedMapOf<LocalTime, MutableList<BookingEntity>>()

        var sessionsPassed = 1
        for ((i, booking) in bookings.withIndex()) {
            val isFree = true
            if (isFree && booking.time in sessionTime) {
                map[sessionTime.start] = (map[sessionTime.start] ?: mutableListOf()).apply { add(booking) }
            }


            if (i / sessionsPassed / slotsPerSession == 1) {
                sessionsPassed++
                val nextEnd = sessionTime.endInclusive.plusSeconds(sessionLengthSec)
                if (nextEnd > config.workingHoursEnd) break

                sessionTime = if (nextEnd <= config.launchTimeStart) {
                    sessionTime.endInclusive..nextEnd
                } else {
                    config.launchTimeEnd..config.launchTimeEnd.plusSeconds(sessionLengthSec)
                }
            }
            map[sessionTime.start] = (map[sessionTime.start] ?: mutableListOf()).apply { add(booking) }
        }

        // limit per session = 3
        // i / sessionPassed / limit == 1
        // i = 0, sessionPassed = 1 => false
        // i = 1, sessionPassed = 1 => false
        // i = 2, sessionPassed = 1 => false
        // i = 3, sessionPassed = 1 => true => sessionsPassed = 2; update session time range; insert booking there
        // i = 4, sessionPassed = 2 => false
        // i = 5, sessionPassed = 2 => false
        // i = 5, sessionPassed = 2 => true => sessionsPassed = 3; update session time range; insert booking there

        val windows = map.map { (time, bookings) ->
            BookingsForTimeWindow(
                startDate = ZonedDateTime.of(date.toLocalDate(), time, moscowZoneId),
                bookings = bookings.map { it.toBookingModel() },
            )
        }

        return@coroutineScope TypedResult.Ok(
            BookingsForDateResponse(
                bookingWindows = windows,
            )
        )
    }

}