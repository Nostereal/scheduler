package com.scheduler.db.dao

import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.BookingEntity
import com.scheduler.db.tables.BookingsTable
import com.scheduler.db.tables.SystemConfig
import com.scheduler.db.tables.SystemConfigs
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.max
import java.time.LocalDate
import java.util.*

interface BookingsDao {

    suspend fun allBookingsByUser(userId: Long): List<BookingEntity>

    suspend fun allBookings(): List<BookingEntity>

    suspend fun allBookingsByDate(date: LocalDate): List<BookingEntity>

    suspend fun insertBooking(booking: BookingDbModel): BookingEntity

    suspend fun deleteBooking(id: UUID): Boolean

}

class BookingDatabase : BookingsDao {

    override suspend fun allBookingsByUser(userId: Long): List<BookingEntity> = dbQuery {
        BookingEntity.find { BookingsTable.ownerId eq userId }.toList()
    }

    override suspend fun allBookings(): List<BookingEntity> = dbQuery {
        BookingEntity.all().toList()
    }

    override suspend fun allBookingsByDate(date: LocalDate): List<BookingEntity> = dbQuery {
        BookingEntity
            .find { BookingsTable.date eq date }
            .orderBy(BookingsTable.time.column to SortOrder.ASC)
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
        val config = SystemConfig.find { SystemConfigs.id eq SystemConfigs.id.max() }.first()

        val newBookingRow = BookingsTable.insert {
            it[date] = booking.date
            it[time.column] = time.toColumn(booking.time)
            it[ownerId] = booking.ownerId
            it[configVersion] = config.id
        }.resultedValues!!.first()

        BookingEntity.wrapRow(newBookingRow)
    }

    override suspend fun deleteBooking(id: UUID): Boolean {
        return BookingsTable.deleteWhere { BookingsTable.id eq id } != 0
    }
}

