package com.scheduler.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class ZonedDateTimesTest {

    @Test
    fun `convert ZonedDateTime to JavaDate and back - value is the same`() {
        val zonedDateTime = ZonedDateTime.now(moscowZoneId)
        val javaDate = zonedDateTime.toJavaDate()
        val convertedZonedDT = javaDate.toZonedDateTime()

        assertThat(convertedZonedDT.zone).isEqualTo(zonedDateTime.zone)
        assertThat(convertedZonedDT.year).isEqualTo(zonedDateTime.year)
        assertThat(convertedZonedDT.month).isEqualTo(zonedDateTime.month)
        assertThat(convertedZonedDT.dayOfMonth).isEqualTo(zonedDateTime.dayOfMonth)
        assertThat(convertedZonedDT.hour).isEqualTo(zonedDateTime.hour)
        assertThat(convertedZonedDT.minute).isEqualTo(zonedDateTime.minute)
        assertThat(convertedZonedDT.second).isEqualTo(zonedDateTime.second)
    }

}