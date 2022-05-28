package com.scheduler.dao

import com.scheduler.DatabaseConfig
import com.scheduler.profile.models.db.Bookings
import com.scheduler.profile.models.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

interface DatabaseFactory {

    fun connect()
    fun close() = Unit

}

class DatabaseFactoryImpl(private val dbConfig: DatabaseConfig) : DatabaseFactory {

    override fun connect() {
//        val driverClassName = config.property("storage.driverClassName").getString()
//        val jdbcUrl = config.property("storage.jdbcURL").getString()
        val database = Database.connect(url = dbConfig.jdbcUrl, driver = dbConfig.driverClassName)
        transaction(database) {
            SchemaUtils.create(Bookings, Users)
        }
    }

}