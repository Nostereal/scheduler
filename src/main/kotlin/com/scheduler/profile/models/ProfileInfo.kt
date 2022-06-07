package com.scheduler.profile.models

import com.scheduler.shared.models.ImageUrl
import kotlinx.serialization.Serializable

@Serializable
data class ProfileInfo(
    val avatar: ImageUrl?,
    val fullName: String,
    val dorm: String,
    val livingRoom: String,
)