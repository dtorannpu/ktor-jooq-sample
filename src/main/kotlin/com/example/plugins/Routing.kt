package com.example.plugins

import com.example.model.CreateTaskRequest
import com.example.service.TaskService
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
    val service by inject<TaskService>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/tasks") {
            val tasks = service.findAll()
            call.respond(HttpStatusCode.OK, tasks)
        }
        post("/tasks") {
            val task = call.receive<CreateTaskRequest>()
            call.respond(HttpStatusCode.Created, task)
        }
    }
}
