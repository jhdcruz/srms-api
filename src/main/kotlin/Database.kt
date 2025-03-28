package ph.dsi.srms

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase(): Database {
    val dbHost = environment.config.property("db.host").getString()
    val dbPort = environment.config.property("db.port").getString()
    val dbUser = environment.config.property("db.username").getString()
    val dbPassword = environment.config.property("db.password").getString()
    val dbName = environment.config.property("db.name").getString()

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:ingres://localhost:AX7/srmsdb"
        driverClassName = "com.ingres.jdbc.IngresDriver"
        maximumPoolSize = 10
    }

    val dataSource = HikariDataSource(config)
    return Database.connect(datasource = dataSource)
}