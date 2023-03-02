//import publishingtls.publishToMaven

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
    id("maven_publish.hummus_publish")
}

group = "com.github.klee0kai.hummus"

android {
    namespace = project.group.toString()
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    api(project(":kotlin_hummus"))

    //timber
    api("com.jakewharton.timber:timber:5.0.1")

    //adapter delegate
    api("com.hannesdorfmann:adapterdelegates4:4.3.2")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
}