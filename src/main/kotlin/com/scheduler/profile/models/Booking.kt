package com.scheduler.profile.models

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: String,
    val timeBracket: TimeBracket,
)