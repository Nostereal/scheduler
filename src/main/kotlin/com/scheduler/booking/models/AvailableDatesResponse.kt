@file:UseSerializers(LocalDateSerializer::class)
package com.scheduler.booking.models

import com.scheduler.shared.serializer.date.LocalDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate


@Serializable
data class AvailableDatesResponse(
    val dates: List<LocalDate>,
)
