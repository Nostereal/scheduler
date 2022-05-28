package com.scheduler.profile.models.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val middleName = varchar("middle_name", 50).nullable()
    val dormNum = varchar("dorm_num", 10).nullable()
    val dormRoom = varchar("dorm_room", 15).nullable()
    val isAdmin = bool("is_admin")
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)
    var firstName by Users.firstName
    var lastName by Users.lastName
    var middleName by Users.middleName
    var dormNum by Users.dormNum
    var dormRoom by Users.dormRoom
    var isAdmin by Users.isAdmin
}