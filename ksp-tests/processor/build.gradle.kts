plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "com.github.klee0kai.ksp.base.sample.processor"
version = libs.versions.hummus.get()

kotlin {
    jvm()
    sourceSets {

        val jvmMain by getting {
            dependencies {
                implementation(project(":ksp-base"))

                implementation(libs.bundles.kotlin)
                implementation(libs.bundles.kotlinpoet)
                implementation(libs.ksp)
            }
        }

    }
}


