import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0" // or latest compatible version
    kotlin("plugin.serialization") version "2.0.0"
    application
}


group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() // Ensure mavenCentral() is included
    gradlePluginPortal()
    maven("https://jitpack.io")


}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.mockk:mockk:1.13.12") // Check for the latest version
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("org.slf4j:slf4j-nop:2.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.2.0")
    implementation("io.ktor:ktor-server-core:2.3.0")
    implementation("io.ktor:ktor-server-netty:2.3.0")
    implementation("io.ktor:ktor-server-host-common:2.3.0")
    implementation("io.ktor:ktor-server-sessions:2.3.0")
    implementation("io.ktor:ktor-server-auth:2.3.0")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.0")
    implementation("io.ktor:ktor-server-call-logging:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")





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


tasks.test {
    useJUnitPlatform()
}






