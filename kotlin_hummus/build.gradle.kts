import publishingtls.publishToMaven

plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
}

group = "com.github.klee0kai.hummus"
version = "0.1-SNAPSHOT"

publishing {
    publishToMaven(project)
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


dependencies {
    testImplementation("junit:junit:4.13.2")
}