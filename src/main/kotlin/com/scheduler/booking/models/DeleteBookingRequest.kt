package com.scheduler.booking.models

import com.scheduler.shared.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class DeleteBookingRequest(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
)
