package com.scheduler.shared.utils

import com.scheduler.shared.models.TypedResult
import io.ktor.http.*

val TypedResult<*>.statusCode: HttpStatusCode
    get() = when (this) {
        is TypedResult.Ok -> HttpStatusCode.OK
        is TypedResult.BadRequest -> HttpStatusCode.BadRequest
        is TypedResult.Unauthorized -> HttpStatusCode.Unauthorized
        is TypedResult.InternalError -> HttpStatusCode.InternalServerError
    }
