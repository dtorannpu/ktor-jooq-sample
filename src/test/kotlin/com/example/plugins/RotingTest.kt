package com.example.plugins

import com.example.database.DatabaseModule
import com.example.repository.Module.repositoryModules
import com.example.usecase.Module.serviceModules
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

class RotingTest : RoutingTestBase() {
    private val databaseModule = DatabaseModule(db.host, db.firstMappedPort, db.username, db.password, db.databaseName).databaseModules()

    init {
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

                client.get("/tasks") { }.apply {
                    status shouldBe HttpStatusCode.OK
                    contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
                    bodyAsText() shouldBe """[]"""
                }
            }
        }
    }
}
