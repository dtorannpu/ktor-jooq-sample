import org.flywaydb.gradle.task.AbstractFlywayTask

buildscript {
    dependencies {
        classpath(libs.org.flywaydb.flyway.database.postgresql)
    }
}

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.io.ktor.plugin)
    alias(libs.plugins.org.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.org.jooq.jooq.codegen.gradle)
    alias(libs.plugins.org.flywaydb.flyway)
    alias(libs.plugins.co.uzzu.dotenv.gradle)
    alias(libs.plugins.jacoco)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin.sourceSets.main {
    kotlin.srcDirs(layout.buildDirectory.dir("generated-sources/jooq"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.io.ktor.ktor.server.core)
    implementation(libs.io.ktor.ktor.server.netty)
    implementation(libs.io.ktor.ktor.server.content.negotiation)
    implementation(libs.io.ktor.ktor.serialization.kotlinx.json)
    implementation(libs.io.ktor.ktor.server.request.validation)
    implementation(libs.io.ktor.ktor.server.status.pages)
    implementation(libs.ch.qos.logback.logback.classic)
    implementation(libs.org.jooq.jooq)
    implementation(libs.org.jooq.jooq.kotlin)
    implementation(libs.org.jooq.jooq.kotlin.coroutines)
    implementation(libs.io.insert.koin.koin.ktor)
    implementation(libs.io.insert.koin.koin.logger.slf4j)
    implementation(libs.io.r2dbc.r2dbc.pool)
    implementation(libs.io.projectreactor.kotlin.reactor.kotlin.extensions)
    implementation(libs.io.ktor.ktor.client.content.negotiation)
    testImplementation(libs.org.flywaydb.flyway.core)
    testImplementation(libs.io.ktor.ktor.server.test.host)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test)
    testImplementation(libs.io.kotest.kotest.runner.junit5)
    testImplementation(libs.io.kotest.kotest.assertions.core)
    testImplementation(libs.io.kotest.kotest.assertions.json)
    testImplementation(libs.io.kotest.extensions.kotest.assertions.ktor)
    testImplementation(libs.io.kotest.extensions.kotest.extensions.testcontainers)
    testImplementation(libs.org.testcontainers.postgresql)
    testImplementation(libs.org.assertj.assertj.db)
    runtimeOnly(libs.org.postgresql.postgresql)
    testRuntimeOnly(libs.org.postgresql.postgresql)
    testRuntimeOnly(libs.org.flywaydb.flyway.database.postgresql)
    runtimeOnly(libs.org.postgresql.r2dbc.postgresql)
    jooqCodegen(libs.org.postgresql.postgresql)
    jooqCodegen(libs.org.jooq.jooq)
    jooqCodegen(libs.org.jooq.jooq.meta)
    jooqCodegen(libs.org.jooq.jooq.codegen)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

flyway {
    url = "jdbc:postgresql://localhost:${env.DB_PORT.value}/${env.DB_NAME.value}"
    user = env.DB_USER.value
    password = env.DB_PASSWORD.value
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:${env.DB_PORT.value}/${env.DB_NAME.value}"
            user = env.DB_USER.value
            password = env.DB_PASSWORD.value
        }
        generator {
            strategy {
                name = "org.jooq.codegen.example.JPrefixGeneratorStrategy"
            }
            database {

                // The database dialect from jooq-meta. Available dialects are
                // named org.jooq.meta.[database].[database]Database.
                //
                // Natively supported values are:
                //
                // org.jooq.meta.ase.ASEDatabase
                // org.jooq.meta.auroramysql.AuroraMySQLDatabase
                // org.jooq.meta.aurorapostgres.AuroraPostgresDatabase
                // org.jooq.meta.cockroachdb.CockroachDBDatabase
                // org.jooq.meta.db2.DB2Database
                // org.jooq.meta.derby.DerbyDatabase
                // org.jooq.meta.firebird.FirebirdDatabase
                // org.jooq.meta.h2.H2Database
                // org.jooq.meta.hana.HANADatabase
                // org.jooq.meta.hsqldb.HSQLDBDatabase
                // org.jooq.meta.ignite.IgniteDatabase
                // org.jooq.meta.informix.InformixDatabase
                // org.jooq.meta.ingres.IngresDatabase
                // org.jooq.meta.mariadb.MariaDBDatabase
                // org.jooq.meta.mysql.MySQLDatabase
                // org.jooq.meta.oracle.OracleDatabase
                // org.jooq.meta.postgres.PostgresDatabase
                // org.jooq.meta.redshift.RedshiftDatabase
                // org.jooq.meta.snowflake.SnowflakeDatabase
                // org.jooq.meta.sqldatawarehouse.SQLDataWarehouseDatabase
                // org.jooq.meta.sqlite.SQLiteDatabase
                // org.jooq.meta.sqlserver.SQLServerDatabase
                // org.jooq.meta.sybase.SybaseDatabase
                // org.jooq.meta.teradata.TeradataDatabase
                // org.jooq.meta.trino.TrinoDatabase
                // org.jooq.meta.vertica.VerticaDatabase
                //
                // This value can be used to reverse-engineer generic JDBC DatabaseMetaData (e.g. for MS Access)
                //
                // org.jooq.meta.jdbc.JDBCDatabase
                //
                // This value can be used to reverse-engineer standard jOOQ-meta XML formats
                //
                // org.jooq.meta.xml.XMLDatabase
                //
                // This value can be used to reverse-engineer schemas defined by SQL files
                // (requires jooq-meta-extensions dependency)
                //
                // org.jooq.meta.extensions.ddl.DDLDatabase
                //
                // This value can be used to reverse-engineer schemas defined by JPA annotated entities
                // (requires jooq-meta-extensions-hibernate dependency)
                //
                // org.jooq.meta.extensions.jpa.JPADatabase
                //
                // This value can be used to reverse-engineer schemas defined by Liquibase migration files
                // (requires jooq-meta-extensions-liquibase dependency)
                //
                // org.jooq.meta.extensions.liquibase.LiquibaseDatabase
                //
                // You can also provide your own org.jooq.meta.Database implementation
                // here, if your database is currently not supported
                name = "org.jooq.meta.postgres.PostgresDatabase"

                // All elements that are generated from your schema (A Java regular expression.
                // Use the pipe to separate several expressions) Watch out for
                // case-sensitivity. Depending on your database, this might be
                // important!
                //
                // You can create case-insensitive regular expressions using this syntax: (?i:expr)
                //
                // Whitespace is ignored and comments are possible.
                includes = ""

                // All elements that are excluded from your schema (A Java regular expression.
                // Use the pipe to separate several expressions). Excludes match before
                // includes, i.e. excludes have a higher priority
                excludes = "flyway_schema_history"

                // The schema that is used locally as a source for meta information.
                // This could be your development schema or the production schema, etc
                // This cannot be combined with the schemata element.
                //
                // If left empty, jOOQ will generate all available schemata. See the
                // manual's next section to learn how to generate several schemata
                inputSchema = "public"
            }

            // Generation flags: See advanced configuration properties
            generate {
                name = "org.jooq.codegen.KotlinGenerator"
                // Tell the KotlinGenerator to generate properties in addition to methods for these paths. Default is true.
                isImplicitJoinPathsAsKotlinProperties = true

                // Workaround for Kotlin generating setX() setters instead of setIsX() in byte code for mutable properties called
                // <code>isX</code>. Default is true.
                isKotlinSetterJvmNameAnnotationsOnIsPrefix = true

                // Generate POJOs as data classes, when using the KotlinGenerator. Default is true.
                isPojosAsKotlinDataClasses = true

                // Generate non-nullable types on POJO attributes, where column is not null. Default is false.
                isKotlinNotNullPojoAttributes = true

                // Generate non-nullable types on Record attributes, where column is not null. Default is false.
                isKotlinNotNullRecordAttributes = true

                // Generate non-nullable types on interface attributes, where column is not null. Default is false.
                isKotlinNotNullInterfaceAttributes = true

                // Generate defaulted nullable POJO attributes. Default is true.
                isKotlinDefaultedNullablePojoAttributes = false

                // Generate defaulted nullable Record attributes. Default is true.
                isKotlinDefaultedNullableRecordAttributes = false
            }
            target {

                // The destination package of your generated classes (within the
                // destination directory)
                //
                // jOOQ may append the schema name to this package if generating multiple schemas,
                // e.g. org.jooq.your.packagename.schema1
                // org.jooq.your.packagename.schema2
                packageName = "com.example.db"

                // The destination directory of your generated classes
                // directory = "/path/to/your/dir"
            }
            generate {
                isDaos = false
            }
        }
    }
}

ktlint {
    filter {
        exclude { element ->
            element.file.path.contains("generated-sources")
        }
        include("**/kotlin/**")
    }
}

tasks {
    withType<AbstractFlywayTask> {
        notCompatibleWithConfigurationCache("because https://github.com/flyway/flyway/issues/3550")
    }
}
