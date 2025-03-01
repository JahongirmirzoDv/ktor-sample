package plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import models.TimeTables
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val databaseConfig = environment.config.config("database")
    val jdbcURL = databaseConfig.property("jdbcURL").getString()
    val driverClassName = databaseConfig.property("driverClassName").getString()
    val username = databaseConfig.property("username").getString()
    val password = databaseConfig.property("password").getString()
    
    val config = HikariConfig().apply {
        this.jdbcUrl = jdbcURL
        this.driverClassName = driverClassName
        this.username = username
        this.password = password
        this.maximumPoolSize = 10
        this.isAutoCommit = false
        this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        this.validate()
    }
    
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    
    // Create tables
    transaction {
        SchemaUtils.create(TimeTables)
    }
}