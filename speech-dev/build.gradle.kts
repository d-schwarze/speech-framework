plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = "de.speech"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    implementation("org.jetbrains:annotations:20.1.0")
    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation(project(":speech-core"))
    implementation("org.apache.commons:commons-lang3:3.0")
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
shadowJar.apply {
    archiveBaseName.set(project.name + "-all")
}
