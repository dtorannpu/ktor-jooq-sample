package com.example.repository

import com.example.db.tables.records.JTaskRecord
import com.example.db.tables.references.TASK
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.DATABASE
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.HOST
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PORT
import io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.impl.DSL

class TaskRepository {
    suspend fun selectById(id: Int): JTaskRecord? {
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
        return dslContext
            .selectFrom(TASK)
            .where(
                TASK.ID
                    .eq(id),
            ).awaitFirstOrNull()
    }
}
