package com.scheduler.auth

import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.polytech.PolytechApi
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable

class AuthRepository(
    private val polytechApi: PolytechApi,
    private val usersDao: UsersDao,
    private val appScope: CoroutineScope,
) {

    suspend fun getToken(login: String, password: String): PolytechAuthResponse {
        return polytechApi.getToken(login, password)
    }

    suspend fun getTokenWithUserId(login: String, password: String): AuthResponse = supervisorScope {
        val token = polytechApi.getToken(login, password).token
        val userInfoDef = async(Dispatchers.IO) { polytechApi.getUserInfo(token) }
        val dormitoryDef = async(Dispatchers.IO) { polytechApi.getDormInfo(token) }

        val user = userInfoDef.await().user
        val dormitory = dormitoryDef.await()

        val userDbModel = UserDbModel.from(user, dormitory)
        appScope.launch { usersDao.insertUserIfNotExist(userDbModel) }

        AuthResponse(
            userId = user.id,
            token = token,
        )
    }

}

@Serializable
data class AuthResponse(
    val userId: Long,
    val token: String,
)