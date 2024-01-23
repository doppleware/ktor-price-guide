
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val postgres_version: String by project
val h2_version: String by project
val exposedVersion: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
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
    maven(url = "https://jitpack.io")

}

dependencies {

    //Databases
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")

    //Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    //Serialization
    implementation ("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")

    //DI
    implementation ("io.insert-koin:koin-ktor:3.5.3")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.3")

    //Log
    implementation("ch.qos.logback:logback-classic:$logback_version")

    //Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("io.github.serpro69:kotlin-faker:1.15.0")

    //OTEL
    implementation("io.opentelemetry:opentelemetry-extension-kotlin:1.29.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-2.0:1.29.0-alpha")
    implementation("io.opentelemetry:opentelemetry-api:1.29.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.26.0")

    //Scraping
    implementation("it.skrape:skrapeit:1.3.0-alpha.1")

    //Rabbit
    implementation ("com.rabbitmq:amqp-client:5.20.0")
    implementation ("com.github.JUtupe:ktor-rabbitmq:0.4.0")

    //Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation ("org.testcontainers:rabbitmq:1.19.3")
    testImplementation ("org.testcontainers:postgresql:1.19.3")




}
