package com.example.database

import org.jooq.DSLContext
import kotlin.coroutines.coroutineContext

class TransactionAwareDSLContext(
    private val dslContext: DSLContext,
) {
    suspend fun get(): DSLContext = coroutineContext.getDSLContext() ?: this.dslContext
}
