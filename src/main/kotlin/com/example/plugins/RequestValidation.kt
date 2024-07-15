package com.example.plugins

import com.example.model.CreateTask
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<CreateTask> { task ->
            val errors = mutableListOf<String>()
            if (task.title.isNullOrBlank()) {
                errors.add("titelは必須です。")
            }

            if (task.description.isNullOrBlank()) {
                errors.add("descriptionは必須です。")
            }

            if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors.toList())
            }
        }
    }
}
