package com.example.plugins

import com.example.model.CreateTaskRequest
import com.example.usecase.TaskUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val useCase by inject<TaskUseCase>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/tasks") {
            val tasks = useCase.findAll()
            call.respond(HttpStatusCode.OK, tasks)
        }
        post("/tasks") {
            val task = call.receive<CreateTaskRequest>()
            call.respond(HttpStatusCode.Created, useCase.create(task))
        }
    }
}
