package com.scheduler.profile.models

import com.scheduler.shared.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ProfileBooking(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val timeBracket: TimeBracket,
)