package com.scheduler.config

import com.scheduler.db.dao.SystemConfigDao
import com.scheduler.isdayoff.IsDayOff
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.dayTypesByRange
import com.scheduler.utils.moscowZoneId
import com.scheduler.utils.toLocalDate
import java.time.LocalDate
import java.time.ZonedDateTime

class SystemConfigRepository(
    private val systemConfigDao: SystemConfigDao,
    private val isDayOff: IsDayOff,
) {

    suspend fun getAvailableDates(): TypedResult<List<LocalDate>> {
        val now = ZonedDateTime.now(moscowZoneId).toLocalDate()
        val config = systemConfigDao.getConfigForDate(now)

        val dates = isDayOff.dayTypesByRange(
            now,
            now.plusDays(config.maxDaysAhead.toLong())
        )
            .filter { it.dayType.isWorkingDay == true }
            .map { it.date.toLocalDate() }

        return TypedResult.Ok(dates)
    }

}