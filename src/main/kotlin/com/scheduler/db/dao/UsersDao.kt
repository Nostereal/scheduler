package com.scheduler.db.dao

import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.User
import com.scheduler.db.tables.Users
import org.jetbrains.exposed.sql.insertIgnore

interface UsersDao {

    suspend fun getUserById(userId: Long): User?

    suspend fun insertUserIfNotExist(user: UserDbModel)

}

class UsersDatabase : UsersDao {

    override suspend fun getUserById(userId: Long) = dbQuery {
        User.findById(userId)
    }

    override suspend fun insertUserIfNotExist(user: UserDbModel): Unit = dbQuery {
        Users.insertIgnore {
            it[id] = user.id
            it[firstName] = user.firstName
            it[middleName] = user.middleName
            it[lastName] = user.lastName
            it[dormNum] = user.dormNum
            it[dormRoom] = user.dormRoom
            it[isAdmin] = user.isAdmin
        }
    }

}