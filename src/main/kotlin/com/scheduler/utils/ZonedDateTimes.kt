package com.scheduler.utils

import java.time.ZonedDateTime
import java.util.*

fun ZonedDateTime.toJavaDate(): Date = Date.from(toInstant())

fun Date.toZonedDateTime(): ZonedDateTime = toInstant().atZone(moscowZoneId)