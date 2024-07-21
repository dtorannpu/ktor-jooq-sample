package com.example.usecase

import org.koin.dsl.module

object Module {
    val serviceModules =
        module {
            factory<TaskUseCase> { TaskUseCaseImpl(get(), get()) }
        }
}
