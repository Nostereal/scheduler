package com.scheduler.db.tables

import com.scheduler.db.tables.User.Companion.transform
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import java.time.temporal.ChronoUnit
import java.util.*

object BookingsTable : UUIDTable(name = "bookings") {
    val date = date("date")
    val time = time("time").transform(
        { it.truncatedTo(ChronoUnit.MINUTES) },
        { it.truncatedTo(ChronoUnit.MINUTES) }
    )
    val ownerId = reference("owner_id", Users)
    val configVersion = reference("config_ver", SystemConfigs)
}

class BookingEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<BookingEntity>(BookingsTable)

    var date by BookingsTable.date
    var time by BookingsTable.time
    var ownerId by User referencedOn BookingsTable.ownerId
    var configVersion by SystemConfig referencedOn SystemConfigs.id
}