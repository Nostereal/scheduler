package com.scheduler.db.dao

import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.Booking
import com.scheduler.db.tables.Bookings
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import java.time.OffsetDateTime

interface BookingsDao {

    suspend fun allBookingsByUser(userId: Long): List<Booking>

    suspend fun allBookings(): List<Booking>

    suspend fun allBookingsByDate(date: OffsetDateTime): List<Booking>

    suspend fun insertBooking(booking: BookingDbModel)

}

class BookingDatabase : BookingsDao {

    override suspend fun allBookingsByUser(userId: Long): List<Booking> = dbQuery {
        Booking.find { Bookings.ownerId eq userId }.toList()
    }

    override suspend fun allBookings(): List<Booking> = dbQuery {
        Booking.all().toList()
    }

    override suspend fun allBookingsByDate(date: OffsetDateTime): List<Booking> = dbQuery {
        Booking
            .find { Bookings.date.date() eq date.toLocalDate() }
            .orderBy(Bookings.date to SortOrder.ASC)
            .toList()
    }

    override suspend fun insertBooking(booking: BookingDbModel): Unit = dbQuery {
//        Booking.new {
//            date = booking.date.toLocalDateTime()
//            ownerId = booking.ownerId
//        }
        Bookings.insert {
            it[date] = booking.date.toLocalDateTime()
            it[ownerId] = booking.ownerId
        }
    }
}
