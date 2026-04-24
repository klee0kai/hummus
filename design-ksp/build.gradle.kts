plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
}

group = "com.github.klee0kai.design.ksp"
version = libs.versions.hummus.get()

kotlin {
    jvm()
    sourceSets {

        val jvmMain by getting {
            dependencies {
                implementation(project(":ksp-base"))
                implementation(project(":design-core"))

                implementation(libs.bundles.kotlin)
                implementation(libs.bundles.kotlinpoet)
                implementation(libs.kotlin.ksp)
            }
        }

    }
}


