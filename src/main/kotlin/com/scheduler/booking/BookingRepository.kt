package com.scheduler.booking

import com.scheduler.booking.models.AlertModel
import com.scheduler.booking.models.BookingsForDate
import com.scheduler.booking.models.ScheduleBooking
import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.SystemConfigDao
import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.isdayoff.IsDayOff
import com.scheduler.profile.models.ProfileBooking
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.isDayWorking
import com.scheduler.utils.moscowZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class BookingRepository(
    private val bookingsDao: BookingsDao,
    private val systemConfigDao: SystemConfigDao,
    private val isDayOff: IsDayOff,
) {

    suspend fun deleteBooking(id: UUID): TypedResult<Map<String, String>> {
        val success = bookingsDao.deleteBooking(id)
        return if (success) {
            TypedResult.Ok(emptyMap())
        } else {
            TypedResult.InternalError("No booking with id = $id was found")
        }
    }

    suspend fun createBooking(userId: Long, date: LocalDate, sessionNum: Short): TypedResult<ProfileBooking> = coroutineScope {
        if (date < ZonedDateTime.now(moscowZoneId).toLocalDate()) {
            return@coroutineScope TypedResult.BadRequest("Хорошая попытка, но в прошлое записаться нельзя")
        }

        if (!isDayOff.isDayWorking(date)) {
            return@coroutineScope TypedResult.BadRequest("Этот день нерабочий :(")
        }

        val activeBookingsDef = async(Dispatchers.IO) {
            bookingsDao.allUpcomingBookingsByUser(userId = userId, since = date)
        }
        val configDef = async(Dispatchers.IO) { systemConfigDao.getConfigForDate(date) }

        val maxActiveSessions = configDef.await().activeSessionsLimitPerUser
        if (activeBookingsDef.await().size >= maxActiveSessions) {
            val message = "Вы достигли максимума активных записей :("
            cancel(message)
            return@coroutineScope TypedResult.BadRequest(message)
        }

        val dbBooking = bookingsDao.insertBooking(date = date, sessionNum = sessionNum, ownerId = userId)
        return@coroutineScope TypedResult.Ok(dbBooking)
    }

    suspend fun getBookingsByDate(date: LocalDate): TypedResult<BookingsForDate> = coroutineScope {
        val bookingsDef = async(Dispatchers.IO) { bookingsDao.allBookingsByDate(date) }
        val configDef = async(Dispatchers.IO) { systemConfigDao.getConfigForDate(date) }

        if (!isDayOff.isDayWorking(date)) {
            val message = "Passed day isn't working one"
            cancel(message)
            return@coroutineScope TypedResult.BadRequest(message)
        }

        val bookings: Map<Short, List<BookingDbModel>> = bookingsDef.await().groupBy { it.sessionNum }
        val config = configDef.await()

        val dayStart = config.workingHoursStart
        val dayEnd = config.workingHoursEnd
        val sessionSecs = config.sessionSeconds
        val launchStart = config.launchTimeStart
        val launchEnd = config.launchTimeEnd
        val slotsPerSession = config.slotsPerSession.toInt()

        val secsBeforeLaunch = dayStart.until(launchStart, ChronoUnit.SECONDS)
        val secsAfterLaunch = launchEnd.until(dayEnd, ChronoUnit.SECONDS)
        val sessionsBeforeLaunchCount = secsBeforeLaunch / sessionSecs
        val sessionsAfterLaunchCount = secsAfterLaunch / sessionSecs

        val sessionsBeforeLaunch = (0 until sessionsBeforeLaunchCount).map { sessionIndex ->
            val sessionNum = (sessionIndex + 1).toShort()
            val sessionBookings = bookings
                .mapToScheduleBookings(sessionNum)
                .take(slotsPerSession)

            BookingsForDate.Session.Open(
                startTime = dayStart.plusSeconds(sessionIndex * sessionSecs),
                sessionNum = sessionNum,
                bookings = sessionBookings,
                maxBookingsPerSession = slotsPerSession,
            )
        }

        val launchSession = BookingsForDate.Session.Launch(
            startTime = launchStart,
            banner = AlertModel(title = "Закрыто на обед", body = "Сейчас работники кушают, но скоро всё откроется ;)"),
        )

        val sessionsAfterLaunch = (0 until sessionsAfterLaunchCount).map { sessionIndex ->
            val sessionNum = (sessionIndex + sessionsBeforeLaunchCount).toShort()
            val sessionBookings = bookings
                .mapToScheduleBookings(sessionNum)
                .take(slotsPerSession)

            BookingsForDate.Session.Open(
                startTime = launchEnd.plusSeconds(sessionIndex * sessionSecs),
                sessionNum = sessionNum,
                bookings = sessionBookings,
                maxBookingsPerSession = slotsPerSession,
            )
        }

        return@coroutineScope TypedResult.Ok(
            BookingsForDate(
                alert = null,
                date = date,
                sessionSeconds = sessionSecs,
                sessions = sessionsBeforeLaunch + launchSession + sessionsAfterLaunch,
            )
        )
    }

}

fun Map<Short, List<BookingDbModel>>.mapToScheduleBookings(sessionNum: Number): List<ScheduleBooking> {
    return get(sessionNum)
        .orEmpty()
        .map {
            val owner = it.owner
            ScheduleBooking(id = it.uuid, owner = "${owner.firstName} ${owner.lastName} из ${owner.dormRoom}")
        }
}
