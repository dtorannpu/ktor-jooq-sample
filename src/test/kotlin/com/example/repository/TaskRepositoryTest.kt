package com.example.repository

import com.example.database.TransactionAwareDSLContext
import com.example.model.CreateTask
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class TaskRepositoryTest : RepositoryTest() {
    private lateinit var repo: TaskRepository

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
                                )
                        },
                        { connection: Connection -> connection.close().toMono() },
                        { connection: Connection, _: Throwable -> connection.close().toMono() },
                        { connection: Connection -> connection.close().toMono() },
                    ).awaitFirstOrNull()

                repo.findAll().size shouldBeExactly 3
            }
        }
        test("create") {
            runTest {
                val task = CreateTask("title1", "description1")

                val id = repo.create(task)

                Mono
                    .usingWhen(
                        connectionFactory.create().toMono(),
                        { connection ->
                            connection
                                .createStatement("SELECT id, title, description FROM task WHERE id = $1")
                                .bind("$1", id)
                                .execute()
                                .toMono()
                                .flatMap { result ->
                                    result
                                        .map { row, _ ->
                                            row["id"] as Int shouldBe id
                                            row["title"] as String shouldBe task.title
                                            row["description"] as String shouldBe task.description
                                        }.toMono()
                                }
                        },
                        { connection: Connection -> connection.close().toMono() },
                        { connection: Connection, _: Throwable -> connection.close().toMono() },
                        { connection: Connection -> connection.close().toMono() },
                    ).awaitFirstOrNull()
            }
        }
    }
}
