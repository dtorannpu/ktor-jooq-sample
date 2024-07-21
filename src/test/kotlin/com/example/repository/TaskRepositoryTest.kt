package com.example.repository

import com.example.database.TransactionAwareDSLContext
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class TaskRepositoryTest : RepositoryTest() {
    private lateinit var repo: TaskRepository

    fun close(connection: Connection): Mono<Void> = Mono.from(connection.close())

    init {
        beforeEach {
            val t = TransactionAwareDSLContext(dslContext)
            repo = TaskRepositoryImpl(t)
        }

        test("selectById") {
            runTest {
                val id =
                    Mono
                        .usingWhen(
                            connectionFactory.create().toMono(),
                            { connection ->
                                connection
                                    .createStatement("TRUNCATE TABLE task")
                                    .execute()
                                    .toMono()
                                    .then(
                                        connection
                                            .createStatement(
                                                "INSERT INTO task(title, description) VALUES('test', 'hoge') RETURNING id",
                                            ).execute()
                                            .toMono(),
                                    ).flatMap { result -> result.map { row, _ -> row["id"] as Int }.toMono() }
                            },
                            { connection: Connection -> connection.close().toMono() },
                            { connection: Connection, _: Throwable -> connection.close().toMono() },
                            { connection: Connection -> connection.close().toMono() },
                        ).awaitSingle()
                repo.selectById(id).shouldNotBeNull()
            }
        }

        test("findAll") {
            runTest {
                Mono
                    .usingWhen(
                        connectionFactory.create().toMono(),
                        { connection ->
                            connection
                                .createStatement("TRUNCATE TABLE task")
                                .execute()
                                .toMono()
                                .then(
                                    connection
                                        .createStatement(
                                            "INSERT INTO task(title, description) VALUES('test', 'hoge')",
                                        ).execute()
                                        .toMono(),
                                ).then(
                                    connection
                                        .createStatement(
                                            "INSERT INTO task(title, description) VALUES('test', 'hoge')",
                                        ).execute()
                                        .toMono(),
                                ).then(
                                    connection
                                        .createStatement(
                                            "INSERT INTO task(title, description) VALUES('test', 'hoge')",
                                        ).execute()
                                        .toMono(),
                                ).then()
                        },
                        { connection: Connection -> connection.close().toMono() },
                        { connection: Connection, _: Throwable -> connection.close().toMono() },
                        { connection: Connection -> connection.close().toMono() },
                    ).awaitFirstOrNull()

                repo.findAll().size shouldBeExactly 3
            }
        }
    }
}
