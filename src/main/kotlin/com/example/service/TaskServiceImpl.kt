package com.example.service

import com.example.database.TransactionCoroutineOperator
import com.example.model.Task
import com.example.repository.TaskRepository

class TaskServiceImpl(
    private val transactionCoroutineOperator: TransactionCoroutineOperator,
    private val taskRepository: TaskRepository,
) : TaskService {
    override suspend fun findAll(): List<Task> = taskRepository.findAll()

    override suspend fun create() {
        TODO("Not yet implemented")
    }
}
