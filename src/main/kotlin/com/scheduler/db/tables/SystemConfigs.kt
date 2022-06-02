package com.scheduler.db.tables

import com.scheduler.utils.moscowZoneId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.time
import java.time.ZonedDateTime

object SystemConfigs : IntIdTable(name = "system_config", columnName = "version") {
    val sessionSeconds = integer("session_length_sec")
    val createdAt = datetime("created_at").clientDefault { ZonedDateTime.now(moscowZoneId).toLocalDateTime() }
    val workingHoursStart = time("opened_at")
    val workingHoursEnd = time("closed_at")
    val launchTimeStart = time("launch_start_at")
    val launchTimeEnd = time("launch_end_at")
    val maxDaysAhead = short("max_days_ahead")
    val slotsPerSession = short("slots_per_session")
}

class SystemConfig(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SystemConfig>(SystemConfigs)
    val sessionSeconds by SystemConfigs.sessionSeconds
    val createdAt by SystemConfigs.createdAt
    val workingHoursStart by SystemConfigs.workingHoursStart
    val workingHoursEnd by SystemConfigs.workingHoursEnd
    val launchTimeStart by SystemConfigs.launchTimeStart
    val launchTimeEnd by SystemConfigs.launchTimeEnd
    val maxDaysAhead by SystemConfigs.maxDaysAhead
    val slotsPerSession by SystemConfigs.slotsPerSession
}