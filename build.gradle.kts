plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.google.cloud.tools.jib") version "2.7.0"
}

group = "fr.fteychene.teaching.cloud.sample"
version = "0.1.0-SNAPSHOT"

project.setProperty("mainClassName", "fr.fteychene.teaching.cloud.sample.MainKt")

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(platform("org.http4k:http4k-bom:3.280.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-client-websocket")
    implementation("org.http4k:http4k-template-handlebars")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.postgresql:postgresql:42.2.18")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        targetCompatibility = "11"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

jib {
    from {
        image = "openjdk:11-jdk-buster"
    }
    to {
        image = "fteychene/cloud-failer"
        tags = setOf("${project.version}", "latest")
    }
    container {
        mainClass = "fr.fteychene.teaching.cloud.sample.MainKt"
        ports = listOf("8080")
        format = com.google.cloud.tools.jib.api.buildplan.ImageFormat.OCI
    }
}

tasks.register<Task>("stage") {
    dependsOn(tasks.shadowJar)
}