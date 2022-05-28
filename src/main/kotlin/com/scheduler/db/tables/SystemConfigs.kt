package com.scheduler.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SystemConfigs : IntIdTable(name = "system_config", columnName = "version") {
    val sessionSeconds = integer("session_length_sec")
}

class SystemConfig(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SystemConfig>(SystemConfigs)
    var sessionSeconds by SystemConfigs.sessionSeconds
}