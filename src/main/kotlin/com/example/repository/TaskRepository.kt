package com.example.repository

import com.example.db.tables.records.JTaskRecord
import com.example.model.Task

interface TaskRepository {
    suspend fun selectById(id: Int): JTaskRecord?

    suspend fun findAll(): List<Task>
}
