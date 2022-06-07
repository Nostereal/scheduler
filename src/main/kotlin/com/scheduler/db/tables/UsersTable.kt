package com.scheduler.db.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object UsersTable : IdTable<Long>(name = "users") {
    override val id = long("id").entityId()
    override val primaryKey = PrimaryKey(id)
    val avatar = varchar("avatar_url", 2083).nullable()
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val middleName = varchar("middle_name", 50).nullable()
    val dormNum = varchar("dorm_num", 10).nullable()
    val dormRoom = varchar("dorm_room", 15).nullable()
    val isAdmin = bool("is_admin")
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UsersTable)
    var avatar by UsersTable.avatar
    var firstName by UsersTable.firstName
    var lastName by UsersTable.lastName
    var middleName by UsersTable.middleName
    var dormNum by UsersTable.dormNum
    var dormRoom by UsersTable.dormRoom
    var isAdmin by UsersTable.isAdmin
}