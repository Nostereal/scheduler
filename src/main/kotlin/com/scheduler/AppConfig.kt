package com.scheduler

data class AppConfig(
    val databaseConfig: DatabaseConfig
)

data class DatabaseConfig(
    val driverClassName: String,
    val jdbcUrl: String,
)