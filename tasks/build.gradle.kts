val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val prometeus_version: String by project
val exposed_version: String by project
val h2_version: String by project
val koin_ktor: String by project
val koin_version: String by project
val koin_ksp_version: String by project
val psql_version: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
    id("org.liquibase.gradle") version "2.2.0"

    id("com.google.devtools.ksp") version "1.7.21-1.0.8" //Config for Koin annotations
}

group = "solutions.dft"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

application {
    mainClass.set("solutions.dft.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    mavenLocal()
}

sourceSets.main {
    java.srcDir("build/generated/ksp/main/kotlin") //Config for Koin annotations
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-openapi:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version") //For using yaml file for configuration
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.postgresql:postgresql:$psql_version")

    implementation("io.ktor:ktor-server-cio-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation ("io.insert-koin:koin-ktor:$koin_ktor") //To use install in App module for koin
    implementation ("io.insert-koin:koin-logger-slf4j:$koin_ktor") //To use slf4logger in install block in App module for koin
    implementation ("io.insert-koin:koin-core:$koin_version") //Base Koin package, needed for Koin annotations
    implementation ("io.insert-koin:koin-annotations:$koin_ksp_version") //Config for Koin annotations
    ksp ("io.insert-koin:koin-ksp-compiler:$koin_ksp_version") //Config for Koin annotations


    liquibaseRuntime("org.liquibase:liquibase-core:4.20.0")
    liquibaseRuntime("org.liquibase:liquibase-gradle-plugin:2.2.0")
    liquibaseRuntime("info.picocli:picocli:4.6.3")
    liquibaseRuntime("org.postgresql:postgresql:$psql_version")
    liquibaseRuntime("ch.qos.logback:logback-core:1.2.9")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.9")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

liquibase {
//    runList = "lq"
    activities.register("tasks") {
        arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "tasks/src/main/resources/db/liquibase/change-log-master.yaml",
            "url" to "jdbc:postgresql://localhost:5432/tasks",
            "username" to "postgres",
            "password" to "mysecretpassword",
        )
    }
}
