package com.scheduler.db.dao

import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.dao.utils.toBookingModel
import com.scheduler.db.tables.BookingEntity
import com.scheduler.db.tables.BookingsTable
import com.scheduler.db.tables.SystemConfig
import com.scheduler.db.tables.User
import com.scheduler.profile.models.Booking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDate
import java.util.*

interface BookingsDao {

    suspend fun allBookingsByUser(userId: Long): List<Booking>

    suspend fun allUpcomingBookingsByUser(userId: Long, since: LocalDate): List<BookingEntity>

    suspend fun allBookings(): List<BookingEntity>

    suspend fun allBookingsByDate(date: LocalDate): List<BookingEntity>

    suspend fun insertBooking(booking: BookingDbModel): Booking

    suspend fun deleteBooking(id: UUID): Boolean

}

class BookingDatabase : BookingsDao {

    override suspend fun allBookingsByUser(userId: Long): List<Booking> = dbQuery {
        BookingEntity
            .find { BookingsTable.ownerId eq userId }
            .toList()
            .map(::toBookingModel)
    }

    override suspend fun allUpcomingBookingsByUser(userId: Long, since: LocalDate): List<BookingEntity> = dbQuery {
        BookingEntity
            .find { (BookingsTable.date greaterEq since) and (BookingsTable.ownerId eq userId) }
            .toList()
    }

    override suspend fun allBookings(): List<BookingEntity> = dbQuery {
        BookingEntity.all().toList()
    }

    override suspend fun allBookingsByDate(date: LocalDate): List<BookingEntity> = dbQuery {
        BookingEntity
            .find { BookingsTable.date eq date }
            .orderBy(BookingsTable.sessionNumber to SortOrder.ASC)
            .toList()
    }

    /*
    * {
    *   "date": "11.11.22",
    *   "windows": [ // sorted list
    *       {
    *           "startTime": "11:00",
    *           "endTime": "11:50", // startTime + config.sessionSeconds
    *           "bookings": [
    *               {
    *                   "owner": {
    *                       "firstName": "abc",
    *                       "secondName": "abc",
    *                       "room": "127",
    *                   }
    *               }
    *           ],
    *       }
    *   ]
    * }
    * */

    override suspend fun insertBooking(booking: BookingDbModel) = dbQuery {
        val config = SystemConfig.all().maxByOrNull { it.id.value }!!
        val owner = User[booking.ownerId]

        val entity = BookingEntity.new {
            date = booking.date
            sessionNum = booking.sessionNum
            ownerId = owner
            configVersion = config
        }

        return@dbQuery toBookingModel(entity)
    }

    override suspend fun deleteBooking(id: UUID): Boolean = dbQuery {
        BookingsTable.deleteWhere { BookingsTable.id eq id } != 0
    }
}
