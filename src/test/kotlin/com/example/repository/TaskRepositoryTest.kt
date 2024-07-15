package com.example.repository

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.test.runTest

class TaskRepositoryTest :
    FunSpec({
        lateinit var repo: TaskRepository
        beforeEach {
            repo = TaskRepository()
        }

        test("selectById") {
            runTest {
                repo.selectById(1)
            }
        }
    })
