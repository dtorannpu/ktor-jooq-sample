package com.example.repository

import com.example.database.TransactionAwareDSLContext
import com.example.db.tables.references.TASK
import com.example.model.CreateTask
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

    override suspend fun create(task: CreateTask): Int =
        dslContext
            .get()
            .insertInto(TASK)
            .columns(TASK.TITLE, TASK.DESCRIPTION)
            .values(task.title, task.description)
            .returningResult(TASK.ID)
            .awaitSingle()
            .map { it[TASK.ID] }

    override suspend fun delete(id: Int): Int =
        dslContext
            .get()
            .deleteFrom(TASK)
            .where(TASK.ID.eq(id))
            .awaitSingle()
}
