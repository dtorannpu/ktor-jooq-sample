package com.example.plugins

import com.example.model.CreateTaskRequest
import com.example.usecase.TaskUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val useCase by inject<TaskUseCase>()
    routing {
        get("/tasks") {
            val tasks = useCase.findAll()
            call.respond(HttpStatusCode.OK, tasks)
        }
        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            useCase.findById(id!!)?.let { call.respond(HttpStatusCode.OK, it) } ?: call.respond(HttpStatusCode.NotFound)
        }
        post("/tasks") {
            val task = call.receive<CreateTaskRequest>()
            call.respond(HttpStatusCode.Created, useCase.create(task))
        }
    }
}
