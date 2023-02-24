//import publishingtls.publishToMaven

plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    id("maven_publish.hummus_publish")
}

group = "com.github.klee0kai.hummus"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


dependencies {
    testImplementation("junit:junit:4.13.2")
}