package com.example.plugins

import com.example.database.DatabaseModule
import com.example.db.tables.references.TASK
import com.example.model.CreateTaskRequest
import com.example.repository.Module.repositoryModules
import com.example.usecase.Module.serviceModules
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.sql.Connection
import java.sql.DriverManager

class RotingTest : RoutingTestBase() {
    private val databaseModule =
        DatabaseModule(db.host, db.firstMappedPort, db.username, db.password, db.databaseName).databaseModules()
    private lateinit var connection: Connection
    private lateinit var dslContext: DSLContext

    init {
        beforeEach {
            connection = DriverManager.getConnection(db.jdbcUrl, db.username, db.password)
            dslContext = DSL.using(connection)
            dslContext.truncate(TASK).execute()
        }

        afterEach {
            connection.close()
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

                client.get("/tasks/4").apply {
                    status shouldBe HttpStatusCode.NotFound
                }
            }
        }

        test("create task") {
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

                val client =
                    createClient {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                val createRequest = CreateTaskRequest("title1", "description1")
                client
                    .post("/tasks") {
                        headers { contentType(ContentType.Application.Json) }
                        setBody(createRequest)
                    }.apply {
                        status shouldBe HttpStatusCode.Created
                        val id = bodyAsText().toIntOrNull()
                        id.shouldNotBeNull()
                        val task = dslContext.selectFrom(TASK).where(TASK.ID.eq(id)).fetchOne()
                        task.shouldNotBeNull()
                        task.id shouldBe id
                        task.title shouldBe createRequest.title
                        task.description shouldBe createRequest.description
                    }
            }
        }
    }
}
