package com.scheduler.profile.models

import com.scheduler.shared.serializer.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime


@Serializable
data class TimeBracket(
    @Serializable(with = OffsetDateTimeSerializer::class)
    val start: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val end: OffsetDateTime,
)