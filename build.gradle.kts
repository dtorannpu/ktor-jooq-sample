val kotlinVersion: String by project
val logbackVersion: String by project
val kotestVersion: String by project
val jooqVersion: String by project
val koinKtor: String by project

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.15.0")
    }
}

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
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

kotlin.sourceSets.main {
    kotlin.srcDirs(layout.buildDirectory.dir("generated-sources/jooq"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-kotlin:$jooqVersion")
    implementation("org.jooq:jooq-kotlin-coroutines:$jooqVersion")
    implementation("io.insert-koin:koin-ktor:$koinKtor")
    implementation("io.insert-koin:koin-logger-slf4j:$koinKtor")
    implementation("io.r2dbc:r2dbc-pool:1.0.1.RELEASE")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    testImplementation("org.flywaydb:flyway-core:10.15.2")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    testRuntimeOnly("org.postgresql:postgresql:42.7.3")
    testRuntimeOnly("org.flywaydb:flyway-database-postgresql:10.15.0")
    runtimeOnly("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")
    jooqCodegen("org.postgresql:postgresql:42.7.3")
    jooqCodegen("org.jooq:jooq:$jooqVersion")
    jooqCodegen("org.jooq:jooq-meta:$jooqVersion")
    jooqCodegen("org.jooq:jooq-codegen:$jooqVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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
