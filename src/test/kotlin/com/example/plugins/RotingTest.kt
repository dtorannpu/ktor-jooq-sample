package com.example.plugins

import com.example.database.DatabaseModule
import com.example.repository.Module.repositoryModules
import com.example.usecase.Module.serviceModules
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.testing.testApplication
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.sql.DriverManager

class RotingTest : RoutingTestBase() {
    private val databaseModule =
        DatabaseModule(db.host, db.firstMappedPort, db.username, db.password, db.databaseName).databaseModules()

    init {
        beforeEach {
            DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("TRUNCATE TABLE task")
                }
            }
        }

        test("tasks") {
            testApplication {
                install(Koin) {
                    slf4jLogger()
                    modules(databaseModule)
                    modules(repositoryModules)
                    modules(serviceModules)
                }

                application {
                    configureSerialization()
                    configureRequestValidation()
                    configStatusPages()
                    configureRouting()
                }

                DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { connection ->
                    connection.createStatement().use { statement ->
                        statement.execute(
                            """
                            INSERT INTO task(id, title, description)
                            OVERRIDING SYSTEM VALUE
                            VALUES
                                (1, 'title1', 'description1'),
                                (2, 'title2', 'description2'),
                                (3, 'title3', 'description3')
                            """.trimIndent(),
                        )
                    }
                }

                client.get("/tasks").apply {
                    status shouldBe HttpStatusCode.OK
                    contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
                    bodyAsText() shouldEqualJson
                        """
                        [
                        {"id":1,"title":"title1","description":"description1"},
                        {"id":2,"title":"title2","description":"description2"},
                        {"id":3,"title":"title3","description":"description3"}
                        ]
                        """.trimIndent()
                }
            }
        }

        test("find by task id") {
            testApplication {
                install(Koin) {
                    slf4jLogger()
                    modules(databaseModule)
                    modules(repositoryModules)
                    modules(serviceModules)
                }

                application {
                    configureSerialization()
                    configureRequestValidation()
                    configStatusPages()
                    configureRouting()
                }

                DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { connection ->
                    connection.createStatement().use { statement ->
                        statement.execute(
                            """
                            INSERT INTO task(id, title, description)
                            OVERRIDING SYSTEM VALUE
                            VALUES
                                (1, 'title1', 'description1'),
                                (2, 'title2', 'description2'),
                                (3, 'title3', 'description3')
                            """.trimIndent(),
                        )
                    }
                }

                client.get("/tasks/2").apply {
                    status shouldBe HttpStatusCode.OK
                    contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
                    bodyAsText() shouldEqualJson
                        """{"id":2,"title":"title2","description":"description2"}"""
                }
            }
        }

        test("find by task id not found") {
            testApplication {
                install(Koin) {
                    slf4jLogger()
                    modules(databaseModule)
                    modules(repositoryModules)
                    modules(serviceModules)
                }

                application {
                    configureSerialization()
                    configureRequestValidation()
                    configStatusPages()
                    configureRouting()
                }

                DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { connection ->
                    connection.createStatement().use { statement ->
                        statement.execute(
                            """
                            INSERT INTO task(id, title, description)
                            OVERRIDING SYSTEM VALUE
                            VALUES
                                (1, 'title1', 'description1'),
                                (2, 'title2', 'description2'),
                                (3, 'title3', 'description3')
                            """.trimIndent(),
                        )
                    }
                }

                client.get("/tasks/4").apply {
                    status shouldBe HttpStatusCode.NotFound
                }
            }
        }
    }
}
