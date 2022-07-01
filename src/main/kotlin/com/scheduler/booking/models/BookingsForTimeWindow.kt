package com.scheduler.booking.models

import com.scheduler.shared.serializer.UUIDSerializer
import com.scheduler.shared.serializer.date.LocalDateSerializer
import com.scheduler.shared.serializer.date.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Serializable
data class BookingsForDate(
    val alert: AlertModel?,
    val sessionSeconds: Int,
    val canBookSinceSessionNum: Short,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val sessions: List<Session>,
) {

    @Serializable
    sealed interface Session {
        val startTime: LocalTime

        @Serializable
        @SerialName("open")
        data class Open(
            @Serializable(with = LocalTimeSerializer::class)
            override val startTime: LocalTime,
            val sessionNum: Short,
            val maxBookingsPerSession: Int,
            val bookings: List<ScheduleBooking>,
        ) : Session

        @Serializable
        @SerialName("launch")
        data class Launch(
            @Serializable(with = LocalTimeSerializer::class)
            override val startTime: LocalTime,
            val banner: AlertModel?,
        ) : Session

    }
}

@Serializable
data class ScheduleBooking(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val owner: String,
)

@Serializable
data class AlertModel(
    val title: String,
    val body: String,
)

//{
//    // "sessionSeconds": 3000, // 50 min
//    "alert": {
//    "title": "Короткий день",
//    "body": "Сегодня стирка закроется в 16:00.\nБудьте внимательны!"
//},
//    "date": "02.07.2022",
//    "sessions": [
//    {
//        "status": "open",
//        "content": {
//        "number": 1,
//        "bookings": []
//    }
//    },
//    {
//        "status": "open",
//        "content": {
//        "startTime": "11:00",
//        "bookings": [
//        {
//            "id": "8b8bdd0d-7b1f-47c2-9bcf-67ae2888d57e",
//            "owner": "Михалев Артём из 127"
//        }
//        ]
//    }
//    },
