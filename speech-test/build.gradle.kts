plugins {
    `java-library`
}

group = "de.speech"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api(project(":speech-core"))
    api(project(":speech-dev"))
    api(project(":speech-worker"))
    implementation(files("src/test/resources/SpeechToText.jar"))
    implementation(files("src/test/resources/CMUSphinxService-0.1.jar"))

    implementation(files("src/test/resources/VoskService-0.1-all.jar"))
    implementation(files("src/test/resources/DeepSpeech2Service-0.1-all.jar"))

    implementation("com.google.code.gson:gson:2.8.6")
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()

    maxHeapSize = "4G"
}
