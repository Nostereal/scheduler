package com.scheduler.shared.models

import com.scheduler.shared.serializer.TypedResultSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TypedResultSerializer::class)
sealed class TypedResult<T> {
    abstract val result: T
    abstract val status: String

    @Serializable
    data class Ok<T : Any>(
        override val result: T,
        override val status: String = "ok",
    ) : TypedResult<T>()

    @Serializable
    data class BadRequest(
        override val result: ErrorWithMessage,
        override val status: String = "bad-request",
    ) : TypedResult<ErrorWithMessage>() {

        constructor(message: String) : this(result = ErrorWithMessage(message))

    }

    @Serializable
    data class InternalError(
        override val result: ErrorWithMessage,
        override val status: String = "internal-error",
    ) : TypedResult<ErrorWithMessage>() {

        constructor(message: String) : this(result = ErrorWithMessage(message))

    }

}
