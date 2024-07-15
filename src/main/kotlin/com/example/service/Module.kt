package com.example.service

import org.koin.dsl.module

object Module {
    val serviceModules =
        module {
            factory<TaskService> { TaskServiceImpl(get(), get()) }
        }
}
