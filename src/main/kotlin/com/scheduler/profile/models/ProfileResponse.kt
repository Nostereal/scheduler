package com.scheduler.profile.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val profileInfo: ProfileInfo,
    val bookings: List<Booking>,
)
