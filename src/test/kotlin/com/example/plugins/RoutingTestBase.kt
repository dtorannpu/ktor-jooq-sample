package com.example.plugins

import io.kotest.core.spec.style.FunSpec
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

abstract class RoutingTestBase : FunSpec() {
    init {
        beforeSpec {
        }
    }

    companion object {
        val db =
            PostgreSQLContainer("postgres:18.1").apply {
                waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(1)))
            }

        init {
            db.start()

            Flyway
                .configure()
                .dataSource(db.jdbcUrl, db.username, db.password)
                .load()
                .migrate()
        }
    }
}
