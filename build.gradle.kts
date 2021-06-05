import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

val kotlinTegramBotVersion = "6.0.4"
val junitVersion = "5.4.2"

group = "it.vashykator.shoppinglist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot", "telegram", kotlinTegramBotVersion)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = junitVersion)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
}

application {
    mainClass.set("it.vashykator.shoppinglist.MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()

    with(testLogging) {
        showStandardStreams = true
        exceptionFormat = FULL
        events(PASSED, SKIPPED, FAILED)
    }
}
