package com.scheduler.profile

import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

fun Route.profileHandler(di: DI) {
    val profileRepository by di.instance<ProfileRepository>()

    get("api/1/profile") {
        val params = parseQueryString(call.request.queryString())

        val token = params["token"]
        val userId = params["userId"]
        if (userId != null) {
            val profileInfo = profileRepository.getProfileInfo(userId.toLong())
            call.respond(profileInfo)
        } else if (token != null) {
            val profileInfo = profileRepository.getProfileInfo(token)
            call.respond(profileInfo)
        } else {
            call.respond(TypedResult.BadRequest.withDefaultError)
        }
    }

}