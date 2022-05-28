package com.scheduler.utils

import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

suspend fun ApplicationCall.respondTypedResult(result: TypedResult<*>) {
    respond(result)
}

@Serializable
sealed class TypedResult<T : Any>(val status: String) {
    abstract val result: T

    @Serializable
    data class Ok<T : Any>(override val result: T) : TypedResult<T>("ok")

    @Serializable
    data class BadRequest(override val result: String) : TypedResult<String>("bad-request")

    @Serializable
    data class InternalError(override val result: String) : TypedResult<String>("internal-error")

}