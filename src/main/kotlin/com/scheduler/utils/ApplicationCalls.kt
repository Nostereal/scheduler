package com.scheduler.utils

import com.scheduler.plugins.serverJsonConfiguration
import com.scheduler.shared.models.TypedResult
import com.scheduler.shared.serializer.TypedResultSerializer
import com.scheduler.shared.utils.statusCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

suspend inline fun <reified T> ApplicationCall.respond(result: TypedResult<T>) = respond(result.statusCode, result)

suspend inline fun <reified T> ApplicationCall.respond(
    serializer: TypedResultSerializer<T>,
    result: TypedResult<T>,
    json: Json = serverJsonConfiguration,
) = respondText(
    status = result.statusCode,
    contentType = ContentType.Application.Json,
    text = json.encodeToString(serializer, result),
)
