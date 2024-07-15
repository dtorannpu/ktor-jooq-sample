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

object Module {
    val databaseModules =
        module {
            single {
                ConnectionFactories.get(
                    ConnectionFactoryOptions
                        .builder()
                        .option(DRIVER, "pool")
                        .option(PROTOCOL, "postgresql")
                        .option(HOST, "localhost")
                        .option(PORT, 5432)
                        .option(USER, "myuser")
                        .option(PASSWORD, "postgres")
                        .option(DATABASE, "mydb")
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
