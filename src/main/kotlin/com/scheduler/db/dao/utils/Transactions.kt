package com.scheduler.db.dao.utils

import com.scheduler.db.tables.BookingEntity
import com.scheduler.profile.calculateSessionStartTime
import com.scheduler.profile.models.Booking
import com.scheduler.profile.models.TimeBracket
import com.scheduler.utils.moscowZoneId
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZonedDateTime

suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun Transaction.toBookingModel(entity: BookingEntity): Booking = with(entity) {
    val startTime = ZonedDateTime.of(
        date,
        calculateSessionStartTime(configVersion, sessionNum),
        moscowZoneId
    )
    return Booking(
        id = id.value,
        timeBracket = TimeBracket(
            start = startTime,
            end = startTime.plusSeconds(configVersion.sessionSeconds.toLong()),
        )
    )
}