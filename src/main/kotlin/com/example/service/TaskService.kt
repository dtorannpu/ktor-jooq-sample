package com.example.service

import com.example.model.Task

interface TaskService {
    suspend fun findAll(): List<Task>
}
