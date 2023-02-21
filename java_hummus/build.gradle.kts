plugins {
    id("java-library")
    id("maven_publish.hummus_publish")
    `maven-publish`
}

group = "com.github.klee0kai.hummus"
version = "0.1-SNAPSHOT"


java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}