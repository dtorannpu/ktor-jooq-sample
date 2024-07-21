package com.example.repository

import com.example.database.TransactionAwareDSLContext
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import reactor.core.publisher.Mono

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
                val connectionMono = Mono.from(pool.create())
                val id =
                    connectionMono
                        .flatMapMany { connection ->
                            Mono
                                .from(
                                    connection
                                        .createStatement("INSERT INTO task(title, description) VALUES($1, $2) RETURNING id")
                                        .bind("$1", "test")
                                        .bind("$2", "hoge")
                                        .execute(),
                                ).flatMap { result ->
                                    Mono.from(result.map { row, _ -> row["id"] as Int })
                                }.doFinally { connection.close() }
                        }.awaitSingle()
                repo.selectById(id).shouldNotBeNull()
            }
        }

        test("findAll") {
            runTest {
                repo.findAll().size shouldBeExactly 5
            }
        }
    }
}
