package com.scheduler.isdayoff

import com.scheduler.isdayoff.enums.LocalesType
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*

/**
 * Builder для установки параметров
 *
 *
 * @property cache Установка кэша. По умолчанию включен. Кэширует весь текущий год
 *
 */
data class IsDayOffBuilder(
    val cache: Boolean = true,
    val httpClient: HttpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://isdayoff.ru/api/")
            header("Host", "isdayoff.ru")
        }
    },
    val locale: LocalesType = LocalesType.RUSSIA,
    val cacheDir: String = "",
    val cacheStorageDays: Int = 30,
    val preHolidaysDay: Int = 0,
    val sixDaysWorkWeek: Int = 0,
    val covidWorkingDays: Int = 0,
)