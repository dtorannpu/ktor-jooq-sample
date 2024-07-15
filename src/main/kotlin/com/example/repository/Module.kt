package com.example.repository

import org.koin.dsl.module

object Module {
    val repositoryModules =
        module {
            factory<TaskRepository> { TaskRepositoryImpl(get()) }
        }
}
