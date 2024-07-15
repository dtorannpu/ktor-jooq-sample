package com.example.repository

import io.kotest.core.spec.style.FunSpec
import io.r2dbc.spi.ConnectionFactories
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
            val pool =
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
            dslContext = DSL.using(pool).dsl()
        }
    }

    companion object {
        private val db = PostgreSQLContainer("postgres:16.2")

        init {
            db.start()
//            Flyway
//                .configure()
//                .dataSource(db.jdbcUrl, db.username, db.password)
//                .load()
//                .migrate()
        }

//        @BeforeAll
//        @JvmStatic
//        fun beforeAll() {
//            Flyway
//                .configure()
//                .dataSource(db.jdbcUrl, db.username, db.password)
//                .load()
//                .migrate()
//        }

//        @DynamicPropertySource
//        @JvmStatic
//        fun registerDBContainer(registry: DynamicPropertyRegistry) {
//            registry.add("spring.datasource.url", db::getJdbcUrl)
//            registry.add("spring.datasource.username", db::getUsername)
//            registry.add("spring.datasource.password", db::getPassword)
//
//            registry.add(
//                "spring.r2dbc.url",
//            ) { String.format("r2dbc:postgresql://%s:%d/%s", db.host, db.firstMappedPort, db.databaseName) }
//            registry.add("spring.r2dbc.username", db::getUsername)
//            registry.add("spring.r2dbc.password", db::getPassword)
//            registry.add("spring.r2dbc.pool.enabled") { true }
//        }
    }
}
