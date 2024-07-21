package com.example.database

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
import org.jooq.impl.DSL
import org.koin.dsl.module

class DatabaseModule(
    private val host: String,
    private val port: Int,
    private val user: String,
    private val password: String,
    private val database: String,
) {
    fun databaseModules() =
        module {
            single {
                ConnectionFactories.get(
                    ConnectionFactoryOptions
                        .builder()
                        .option(DRIVER, "pool")
                        .option(PROTOCOL, "postgresql")
                        .option(HOST, host)
                        .option(PORT, port)
                        .option(USER, user)
                        .option(PASSWORD, password)
                        .option(DATABASE, database)
                        .build(),
                )
            }

            single {
                val pool: ConnectionFactory = get()
                DSL.using(pool).dsl()
            }
            single { TransactionAwareDSLContext(get()) }
            single { TransactionCoroutineOperator(get()) }
        }
}
