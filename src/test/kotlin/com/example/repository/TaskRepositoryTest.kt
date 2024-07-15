package com.example.repository

import com.example.database.TransactionAwareDSLContext
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
import kotlinx.coroutines.test.runTest
import org.jooq.impl.DSL

class TaskRepositoryTest :
    FunSpec({
        lateinit var repo: TaskRepository
        beforeEach {
            val pool =
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
            val dslContext = DSL.using(pool).dsl()
            val t = TransactionAwareDSLContext(dslContext)
            repo = TaskRepositoryImpl(t)
        }

        test("selectById") {
            runTest {
                repo.selectById(1)
            }
        }
    })
