package com.scheduler.profile.models

import com.scheduler.shared.serializer.date.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class TimeBracket(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val start: ZonedDateTime,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val end: ZonedDateTime,
)