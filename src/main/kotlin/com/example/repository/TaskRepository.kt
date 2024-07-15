package com.example.repository

import com.example.model.Task

interface TaskRepository {
    suspend fun selectById(id: Int): Task?

    suspend fun findAll(): List<Task>
}
