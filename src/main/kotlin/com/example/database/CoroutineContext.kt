package com.example.database

import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import org.jooq.DSLContext
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.optionals.getOrNull

private const val KEY = "org.jooq.DSLContext"

fun CoroutineContext.addDSLContext(dslContext: DSLContext): CoroutineContext {
    val reactorContext = this[ReactorContext]
    return if (reactorContext == null) {
        this + ReactorContext(Context.of(KEY, dslContext))
    } else {
        this + reactorContext.context.put(KEY, dslContext).asCoroutineContext()
    }
}

fun CoroutineContext.getDSLContext(): DSLContext? = this[ReactorContext]?.context?.getOrEmpty<DSLContext>(KEY)?.getOrNull()
