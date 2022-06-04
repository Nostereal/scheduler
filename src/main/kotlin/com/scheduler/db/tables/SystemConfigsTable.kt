package com.scheduler.db.tables

import com.scheduler.utils.moscowZoneId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import java.time.ZonedDateTime

object SystemConfigsTable : IntIdTable(name = "system_config", columnName = "version") {
    val sessionSeconds = integer("session_length_sec")
    val createdAt = date("created_at").clientDefault { ZonedDateTime.now(moscowZoneId).toLocalDate() }
    val workingHoursStart = time("opened_at")
    val workingHoursEnd = time("closed_at")
    val launchTimeStart = time("launch_start_at")
    val launchTimeEnd = time("launch_end_at")
    val maxDaysAhead = short("max_days_ahead")
    val slotsPerSession = short("slots_per_session")
    val activeSessionsLimitPerUser = short("active_sessions_limit_per_user")
}

class SystemConfigEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SystemConfigEntity>(SystemConfigsTable)
    val sessionSeconds by SystemConfigsTable.sessionSeconds
    val createdAt by SystemConfigsTable.createdAt
    val workingHoursStart by SystemConfigsTable.workingHoursStart
    val workingHoursEnd by SystemConfigsTable.workingHoursEnd
    val launchTimeStart by SystemConfigsTable.launchTimeStart
    val launchTimeEnd by SystemConfigsTable.launchTimeEnd
    val maxDaysAhead by SystemConfigsTable.maxDaysAhead
    val slotsPerSession by SystemConfigsTable.slotsPerSession
    val activeSessionsLimitPerUser by SystemConfigsTable.activeSessionsLimitPerUser
}