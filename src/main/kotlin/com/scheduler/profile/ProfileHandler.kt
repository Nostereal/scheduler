package com.scheduler.profile

import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

fun Route.getProfile(di: DI) {
    val profileRepository by di.instance<ProfileRepository>()
    get("api/1/profile") {
        val params = parseQueryString(call.request.queryString())

        val token = params["token"] ?: return@get call.respond(
            TypedResult.BadRequest("Token is missing")
        )

        val profileInfo = profileRepository.getProfileInfo(token)
        call.respond(TypedResult.Ok(profileInfo))
    }

}