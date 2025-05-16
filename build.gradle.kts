import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("application")
}

group = "com.lightwhite.paperbot"
version = "1.0"

application {
    mainClass.set("com.lightwhite.paperbot.MainKt")
}

val library: Configuration by configurations.creating
val library0 = configurations.named("library")

configurations {
    implementation.extendsFrom(library0)
    shadow.extendsFrom(library0)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    library("net.mamoe:mirai-core:2.16.0")
    library("top.mrxiaom.mirai:overflow-core:1.0.2")

    library("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    library("ch.qos.logback:logback-classic:1.5.13")
    library("org.slf4j:slf4j-api:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.shadowJar {
    configurations = listOf(library)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}