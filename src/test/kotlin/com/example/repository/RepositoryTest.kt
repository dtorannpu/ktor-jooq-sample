package com.example.repository

import io.kotest.core.spec.style.FunSpec
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.DATABASE
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.HOST
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PORT
import io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer

abstract class RepositoryTest : FunSpec() {
    lateinit var connectionFactory: ConnectionFactory
    lateinit var dslContext: DSLContext

    init {
        beforeSpec {
            Flyway
                .configure()
                .dataSource(db.jdbcUrl, db.username, db.password)
                .load()
                .migrate()
        }
        beforeEach {
            connectionFactory =
                ConnectionFactories.get(
                    ConnectionFactoryOptions
                        .builder()
                        .option(DRIVER, "pool")
                        .option(PROTOCOL, "postgresql")
                        .option(HOST, db.host)
                        .option(PORT, db.firstMappedPort)
                        .option(USER, db.username)
                        .option(PASSWORD, db.password)
                        .option(DATABASE, db.databaseName)
                        .build(),
                )
            dslContext = DSL.using(connectionFactory).dsl()
        }
    }

    companion object {
        private val db = PostgreSQLContainer("postgres:17.6")

        init {
            db.start()
        }
    }
}
