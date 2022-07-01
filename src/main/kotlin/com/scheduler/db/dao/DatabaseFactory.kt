package com.scheduler.db.dao

import com.scheduler.DatabaseConfig
import com.scheduler.db.tables.BookingsTable
import com.scheduler.db.tables.SystemConfigsTable
import com.scheduler.db.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

interface DatabaseFactory {

    fun connect()
    fun close() = Unit

}

class DatabaseFactoryImpl(private val dbConfig: DatabaseConfig) : DatabaseFactory {

    override fun connect() {
        val database = Database.connect(url = dbConfig.jdbcUrl, driver = dbConfig.driverClassName)
        transaction(database) {
//            SchemaUtils.drop(BookingsTable, UsersTable)
            SchemaUtils.create(BookingsTable, UsersTable, SystemConfigsTable)
            SchemaUtils.createMissingTablesAndColumns(BookingsTable, UsersTable, SystemConfigsTable)
        }
    }

}