val kotlinVersion: String by project
val logbackVersion: String by project
val kotestVersion: String by project

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.15.0")
    }
}

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jooq.jooq-codegen-gradle")
    id("org.flywaydb.flyway")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

flyway {
    url = "jdbc:postgresql://localhost:5432/${env.DB_NAME.value}"
    user = env.DB_USER.value
    password = env.DB_PASSWORD.value
}
