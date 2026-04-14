plugins {
    alias(libs.plugins.kotlin.multiplatform)
//    alias(libs.plugins.publish.maven)
//    alias(libs.plugins.publish.crossbox)
}

group = "com.github.klee0kai.ksp.base"
version = libs.versions.hummus.get()

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":hummus"))
                implementation(libs.bundles.kotlin)
                implementation(libs.bundles.kotlinpoet)
                implementation(libs.ksp)
            }
        }

    }
}

