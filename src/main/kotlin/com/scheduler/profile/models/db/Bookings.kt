package com.scheduler.profile.models.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Bookings : UUIDTable() {
    val date = datetime("date")
    val ownerId = reference("owner_id", Users)
}

class Booking(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<Booking>(Bookings)
    var date by Bookings.date
    var ownerId by Bookings.ownerId
}