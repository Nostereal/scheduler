package com.scheduler.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class OffsetDateTimeConversionTest {

    @Test
    fun `convert offset date time to local date time and back - offset is the same`() {
        val zone = ZoneId.of("Europe/Moscow")
        val localDateTime = LocalDateTime.now()
        val zonedLocalDate = ZonedDateTime.of(localDateTime, zone).toLocalDate()
        val manualZone = localDateTime.atZone(zone).toLocalDate()

        assertThat(manualZone).isEqualTo(zonedLocalDate)
    }

    @Test
    fun `aaconvert offset date time to local date time and back - offset is the same`() {
        val zone = ZoneId.of("Europe/Moscow")
        val localDateTime = LocalDateTime.now()
        val zonedLocalDate = ZonedDateTime.now(zone)
        val manualZone = localDateTime.atZone(zone).plusHours(1)

//        assertThat(manualZone).isEqualTo(zonedLocalDate)

        assertEquals(zonedLocalDate, manualZone)
    }

}