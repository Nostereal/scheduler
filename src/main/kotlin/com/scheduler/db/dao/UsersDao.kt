package com.scheduler.db.dao

import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.UserEntity
import com.scheduler.db.tables.UsersTable
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

interface UsersDao {

    suspend fun getUserById(userId: Long): UserEntity?

    suspend fun insertOrUpdateUser(user: UserDbModel)

}

class UsersDatabase : UsersDao {

    override suspend fun getUserById(userId: Long) = dbQuery {
        UserEntity.findById(userId)
    }

    override suspend fun insertOrUpdateUser(user: UserDbModel): Unit = dbQuery {
        if (UserEntity.findById(user.id) != null) {
            UsersTable.update(where = { UsersTable.id eq user.id }) { updateWith(it, user) }
        } else {
            UserEntity.new(user.id) {
                avatar = user.avatar
                firstName = user.firstName
                middleName = user.middleName
                lastName = user.lastName
                dormNum = user.dormNum
                dormRoom = user.dormRoom
                isAdmin = user.isAdmin
            }
        }
    }

    private fun UsersTable.updateWith(it: UpdateBuilder<*>, user: UserDbModel) {
//        it[id] = user.id
        it[avatar] = user.avatar
        it[firstName] = user.firstName
        it[middleName] = user.middleName
        it[lastName] = user.lastName
        it[dormNum] = user.dormNum
        it[dormRoom] = user.dormRoom
        it[isAdmin] = user.isAdmin
    }
}