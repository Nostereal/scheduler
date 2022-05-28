package com.scheduler.polytech.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolytechUserResponse(
    val user: User,
) {

    @Serializable
    data class User(
        val id: Long,
        val avatar: String,
        @SerialName("user_status") val userStatus: Status,
        @SerialName("name") val firstName: String,
        @SerialName("surname") val lastName: String,
        @SerialName("patronymic") val middleName: String?,
    ) {

        @Serializable
        enum class Status {

            @SerialName("stud")
            STUDENT,

        }

    }

}
