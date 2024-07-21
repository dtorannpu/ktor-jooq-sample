package com.example.usecase

import com.example.model.CreateTaskRequest
import com.example.model.Task

interface TaskUseCase {
    suspend fun findAll(): List<Task>

    suspend fun findById(id: Int): Task?

    suspend fun create(createTaskRequest: CreateTaskRequest): Int
}
