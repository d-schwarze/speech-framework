plugins {
    java
    id("idea")
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = "de.speech"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":speech-core"))

    implementation(files("../speech-core/lib/ServiceLibrary-0.1.jar"))

    implementation("org.eclipse.jetty:jetty-client:11.0.0")

    implementation("org.eclipse.jetty:jetty-server:11.0.0")

    implementation("com.google.code.gson:gson:2.8.6")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}

idea {
    module {
        isDownloadJavadoc = true
    }
}

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
shadowJar.apply {
    manifest {
        attributes(mapOf("Main-Class" to "de.speech.worker.Main"))
    }
    archiveBaseName.set(project.name + "-all")
}