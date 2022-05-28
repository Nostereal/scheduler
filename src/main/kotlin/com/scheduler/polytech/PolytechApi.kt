package com.scheduler.polytech

import com.scheduler.auth.PolytechAuthResponse
import com.scheduler.polytech.models.PolytechPaymentsResponse
import com.scheduler.polytech.models.PolytechUserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*


interface PolytechApi {

    suspend fun getDormInfo(token: String): PolytechPaymentsResponse.Contracts.Dormitory

    suspend fun getUserInfo(token: String): PolytechUserResponse

    suspend fun getToken(login: String, password: String): PolytechAuthResponse

}

class PolytechApiImpl(private val client: HttpClient) : PolytechApi {

    override suspend fun getDormInfo(token: String): PolytechPaymentsResponse.Contracts.Dormitory = client
        .get("old/lk_api.php") {
            parameter("getPayments", Unit)
            parameter("token", token)
        }
        .body<PolytechPaymentsResponse>()
        .contracts
        .dormitory
        .first()


    override suspend fun getUserInfo(token: String): PolytechUserResponse = client
        .get("old/lk_api.php") {
            parameter("getUser", Unit)
            parameter("token", token)
        }
        .body()

    override suspend fun getToken(login: String, password: String): PolytechAuthResponse = client
        .submitForm(
            url = "old/lk_api.php",
            formParameters = Parameters.build {
                append("ulogin", login)
                append("upassword", password)
            }
        )
        .body()

}