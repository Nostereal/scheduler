package com.scheduler.utils

import com.scheduler.shared.models.TypedResult
import com.scheduler.shared.utils.statusCode
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.respond(result: TypedResult<T>) = respond(result.statusCode, result)
