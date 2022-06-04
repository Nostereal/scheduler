package com.scheduler.utils

import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

fun ZonedDateTime.toJavaDate(): Date = Date.from(toInstant())

fun Date.toZonedDateTime(): ZonedDateTime = toInstant().atZone(moscowZoneId)

fun Date.toLocalDate(): LocalDate = LocalDate.ofInstant(toInstant(), moscowZoneId)

fun LocalDate.toJavaDate(): Date = Date.from(atStartOfDay(moscowZoneId).toInstant())

