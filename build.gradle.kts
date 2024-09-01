import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24" // or latest compatible version
    kotlin("plugin.serialization") version "1.8.10"
    application
}


group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() // Ensure mavenCentral() is included
    gradlePluginPortal()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "19" // Use Java 21
    targetCompatibility = "19" // Use Java 21
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "19" // Use Java 21
}

application {
    mainClass.set("MainKt")
}



dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}






