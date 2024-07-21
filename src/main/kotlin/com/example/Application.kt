package com.example

import com.example.database.DatabaseModule
import com.example.plugins.configStatusPages
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.repository.Module.repositoryModules
import com.example.usecase.Module.serviceModules
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(DatabaseModule("localhost", 5432, "myuser", "postgres", "mydb").databaseModules())
        modules(repositoryModules)
        modules(serviceModules)
    }
    configureSerialization()
    configureRequestValidation()
    configStatusPages()
    configureRouting()
}
