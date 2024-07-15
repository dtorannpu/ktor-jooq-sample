package com.example.repository

import com.example.database.TransactionAwareDSLContext
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.test.runTest

class TaskRepositoryTest : RepositoryTest() {
    private lateinit var repo: TaskRepository

    init {
        beforeEach {
            val t = TransactionAwareDSLContext(dslContext)
            repo = TaskRepositoryImpl(t)
        }

        test("selectById") {
            runTest {
                repo.selectById(1).shouldNotBeNull()
            }
        }

        test("findAll") {
            runTest {
                repo.findAll().size shouldBeExactly 5
            }
        }
    }
}
