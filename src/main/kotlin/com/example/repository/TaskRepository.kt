package com.example.repository

import com.example.model.CreateTask
import com.example.model.Task

interface TaskRepository {
    suspend fun selectById(id: Int): Task?

    suspend fun findAll(): List<Task>

    suspend fun create(task: CreateTask): Int

    suspend fun delete(id: Int): Int
}
