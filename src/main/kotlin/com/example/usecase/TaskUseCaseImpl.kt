package com.example.usecase

import com.example.database.TransactionCoroutineOperator
import com.example.model.CreateTask
import com.example.model.CreateTaskRequest
import com.example.model.Task
import com.example.repository.TaskRepository

class TaskUseCaseImpl(
    private val transactionCoroutineOperator: TransactionCoroutineOperator,
    private val taskRepository: TaskRepository,
) : TaskUseCase {
    override suspend fun findAll(): List<Task> = taskRepository.findAll()

    override suspend fun findById(id: Int): Task? = taskRepository.selectById(id)

    override suspend fun create(createTaskRequest: CreateTaskRequest) =
        transactionCoroutineOperator.execute {
            val task = CreateTask(createTaskRequest.title!!, createTaskRequest.description!!)
            taskRepository.create(task)
        }
}
