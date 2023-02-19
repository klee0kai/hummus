import publishingtls.publishToMaven

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.majorVersion
    }
}

afterEvaluate {
    publishing {
        publishToMaven(project)
    }
}

dependencies {
    implementation(project(":kotlin_hummus"))

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("androidx.core:core-ktx:1.9.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
}