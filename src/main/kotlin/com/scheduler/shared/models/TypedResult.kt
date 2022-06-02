package com.scheduler.shared.models

import com.scheduler.shared.serializer.TypedResultSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TypedResultSerializer::class)
sealed class TypedResult<in T> {
//    abstract val result: T?
    abstract val status: String

    @Serializable
    data class Ok<T>(
        val result: T?,
        override val status: String = "ok",
    ) : TypedResult<T>()

    @Serializable
    data class BadRequest(
        val result: ErrorWithMessage,
        override val status: String = "bad-request",
    ) : TypedResult<Any>() {

        constructor(message: String) : this(result = ErrorWithMessage(message))

    }

    @Serializable
    data class InternalError(
        val result: ErrorWithMessage,
        override val status: String = "internal-error",
    ) : TypedResult<Any>() {

        constructor(message: String) : this(result = ErrorWithMessage(message))

    }

}
