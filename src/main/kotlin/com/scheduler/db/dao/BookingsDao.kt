package com.scheduler.db.dao

import com.scheduler.db.dao.models.BookingDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.dao.utils.toBookingModel
import com.scheduler.db.tables.BookingEntity
import com.scheduler.db.tables.BookingsTable
import com.scheduler.db.tables.SystemConfigEntity
import com.scheduler.db.tables.UserEntity
import com.scheduler.profile.models.ProfileBooking
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDate
import java.util.*

interface BookingsDao {

    suspend fun allBookingsByUser(userId: Long): List<ProfileBooking>

    suspend fun allUpcomingBookingsByUser(userId: Long, since: LocalDate): List<BookingEntity>

    suspend fun allBookings(): List<BookingEntity>

    suspend fun allBookingsByDate(date: LocalDate): List<BookingDbModel>

    suspend fun insertBooking(
        date: LocalDate,
        sessionNum: Short,
        ownerId: Long,
    ): ProfileBooking

    suspend fun deleteBooking(id: UUID): Boolean

}

class BookingDatabase : BookingsDao {

    override suspend fun allBookingsByUser(userId: Long): List<ProfileBooking> = dbQuery {
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

    override suspend fun allBookingsByDate(date: LocalDate): List<BookingDbModel> = dbQuery {
        BookingEntity
            .find { BookingsTable.date eq date }
            .orderBy(BookingsTable.sessionNumber to SortOrder.ASC)
            .with(BookingEntity::owner)
            .toList()
            .map (BookingDbModel::from)
    }

    override suspend fun insertBooking(
        date: LocalDate,
        sessionNum: Short,
        ownerId: Long,
    ) = dbQuery {
        val config = SystemConfigEntity.all().maxByOrNull { it.id.value }!!
        val owner = UserEntity[ownerId]

        val entity = BookingEntity.new {
            this.date = date
            this.sessionNum = sessionNum
            this.owner = owner
            configVersion = config
        }

        return@dbQuery toBookingModel(entity)
    }

    override suspend fun deleteBooking(id: UUID): Boolean = dbQuery {
        BookingsTable.deleteWhere { BookingsTable.id eq id } != 0
    }
}
