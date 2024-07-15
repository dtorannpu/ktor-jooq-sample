package com.example.repository

import com.example.database.TransactionAwareDSLContext
import com.example.db.tables.references.TASK
import com.example.model.Task
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.Flux

class TaskRepositoryImpl(
    private val dslContext: TransactionAwareDSLContext,
) : TaskRepository {
    override suspend fun selectById(id: Int): Task? =
        dslContext
            .get()
            .selectFrom(TASK)
            .where(TASK.ID.eq(id))
            .awaitFirstOrNull()
            ?.let { Task(it.id!!, it.title!!, it.description!!) }

    override suspend fun findAll(): List<Task> =
        Flux
            .from(dslContext.get().selectFrom(TASK))
            .map {
                Task(it.id!!, it.title!!, it.description!!)
            }.collectList()
            .awaitSingle()
}
