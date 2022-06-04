package com.scheduler.db.dao

import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.SystemConfigEntity
import com.scheduler.db.tables.SystemConfigsTable
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDate

interface SystemConfigDao {

    suspend fun getConfigForDate(date: LocalDate): SystemConfigEntity

}

class SystemConfigDatabase : SystemConfigDao {

    override suspend fun getConfigForDate(date: LocalDate): SystemConfigEntity = dbQuery {
        val configs = SystemConfigEntity.all().orderBy(SystemConfigsTable.id to SortOrder.DESC).toList()

        val actualConfig = configs.first()
        if (date >= actualConfig.createdAt) return@dbQuery actualConfig

        for (i in 0 until configs.lastIndex) {
            val newerConfig = configs[i]
            val olderConfig = configs[i + 1]

            if (date == newerConfig.createdAt) {
                return@dbQuery newerConfig
            } else if (date < newerConfig.createdAt && date >= olderConfig.createdAt) {
                return@dbQuery olderConfig
            }
        }

        throw IllegalStateException("Didn't found a config for the date $date")
    }

}