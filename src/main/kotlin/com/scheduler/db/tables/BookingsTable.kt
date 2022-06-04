package com.scheduler.db.tables

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import java.util.*

object BookingsTable : UUIDTable(name = "bookings") {
    val date = date("date")
//    val time = time("time").transform(
//        { it.truncatedTo(ChronoUnit.MINUTES) },
//        { it.truncatedTo(ChronoUnit.MINUTES) }
//    )
    val sessionNumber = short("session_num")
    val ownerId = reference("owner_id", UsersTable)
    val configVersion = reference("config_ver", SystemConfigsTable)
}

class BookingEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<BookingEntity>(BookingsTable)

    var date by BookingsTable.date
//    var time by BookingsTable.time
    var sessionNum by BookingsTable.sessionNumber
    var owner by UserEntity referencedOn BookingsTable.ownerId
    var configVersion by SystemConfigEntity referencedOn BookingsTable.configVersion
}