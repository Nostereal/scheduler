package com.scheduler.db.tables

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Bookings : UUIDTable(name = "bookings") {
    val date = datetime("date")
    val ownerId = reference("owner_id", Users)
    val configVersion = reference("config_ver", SystemConfigs)
}

class Booking(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<Booking>(Bookings)
    var date by Bookings.date
    var ownerId by User referencedOn Bookings.ownerId
    var configVersion by SystemConfig referencedOn SystemConfigs.id
}