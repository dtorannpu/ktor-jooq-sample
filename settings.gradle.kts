rootProject.name = "ktor-jooq-sample"

pluginManagement {
    val ktorVersion: String by settings
    val ktlintVersion: String by settings
    val jooqVersion: String by settings
    plugins {
        id("io.ktor.plugin") version ktorVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
        id("org.jooq.jooq-codegen-gradle") version jooqVersion
    }
}
