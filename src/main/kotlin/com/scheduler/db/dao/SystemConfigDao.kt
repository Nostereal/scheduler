package com.scheduler.db.dao

import com.scheduler.db.tables.SystemConfig
import com.scheduler.db.tables.SystemConfigs
import org.jetbrains.exposed.sql.SortOrder
import java.time.ZonedDateTime

interface SystemConfigDao {

    suspend fun getConfigForDate(date: ZonedDateTime): SystemConfig

}

class SystemConfigDatabase : SystemConfigDao {

    override suspend fun getConfigForDate(date: ZonedDateTime): SystemConfig {
        val configs = SystemConfig.all().orderBy(SystemConfigs.id to SortOrder.DESC).toList()
        val localDateTime = date.toLocalDateTime()

        val actualConfig = configs.first()
        if (localDateTime >= actualConfig.createdAt) return actualConfig

        for (i in 0 until configs.lastIndex) {
            val newerConfig = configs[i]
            val olderConfig = configs[i + 1]

            if (localDateTime == newerConfig.createdAt) {
                return newerConfig
            } else if (localDateTime < newerConfig.createdAt && localDateTime >= olderConfig.createdAt) {
                return olderConfig
            }
        }

        throw IllegalStateException("Didn't found a config for the date $date")
    }

}