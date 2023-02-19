import publishingtls.publishToMaven

plugins {
    id("com.android.library")
    `maven-publish`
}

group = "com.github.klee0kai.hummus"
version = "0.1-SNAPSHOT"

android {
    namespace = project.group.toString()
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
afterEvaluate {
    publishing {
        publishToMaven(project)
    }
}

dependencies {
    implementation(project(":java_hummus"))
    implementation("com.jakewharton.timber:timber:5.0.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
}