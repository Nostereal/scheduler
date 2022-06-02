package com.scheduler.auth

import com.scheduler.auth.models.AuthRequest
import com.scheduler.shared.models.TypedResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.instance

fun Route.authHandler(di: DI) {
    val authRepository: AuthRepository by di.instance()
    post("api/1/auth") {
        val request: AuthRequest = call.receiveOrNull() ?: return@post call.respond(
            TypedResult.BadRequest(
                "Login or password is missing"
            )
        )

        val response = authRepository.getToken(request.login, request.password)

        call.respond(TypedResult.Ok(response))
    }
}

@Serializable
data class PolytechAuthResponse(
    val token: String,
)