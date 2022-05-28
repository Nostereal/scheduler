package com.scheduler.auth

import com.scheduler.polytech.PolytechApi

class AuthRepository(private val polytechApi: PolytechApi) {

    suspend fun getToken(login: String, password: String): PolytechAuthResponse {
        return polytechApi.getToken(login, password)
    }

}