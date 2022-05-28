package com.scheduler.dao

import com.scheduler.dao.utils.dbQuery
import com.scheduler.profile.models.db.Booking
import com.scheduler.profile.models.db.Bookings
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.OffsetDateTime

interface BookingsDao {

    suspend fun allBookingsByUser(userId: Long): List<Booking>

    suspend fun allBookings(): List<Booking>

    suspend fun allBookingsByDate(date: OffsetDateTime): List<Booking>

}

class BookingDatabase : BookingsDao {

    override suspend fun allBookingsByUser(userId: Long): List<Booking> = dbQuery {
        val query = Bookings.select { Bookings.ownerId eq userId }
        Booking.wrapRows(query).toList()
    }

    override suspend fun allBookings(): List<Booking> = dbQuery {
        Booking.wrapRows(Bookings.selectAll()).toList()
    }

    override suspend fun allBookingsByDate(date: OffsetDateTime): List<Booking> = dbQuery {
        val query = Bookings
            .select { Bookings.date.date() eq date.toLocalDate() }
            .orderBy(Bookings.date, order = SortOrder.ASC)

        Booking.wrapRows(query).toList()
    }
}
