package com.scheduler.polytech.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PolytechPaymentsResponse(
    val contracts: Contracts,
) {

    @Serializable
    data class Contracts(
        val dormitory: List<Dormitory>,
    ) {

        @Serializable
        data class Dormitory(
            val student: String,
            @SerialName("dorm_num") val dormNum: String,
            @SerialName("dorm_room") val dormRoom: String,
        )

    }

}
