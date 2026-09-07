plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.publish.hummus.maven)
}

group = "com.github.klee0kai.hummus.ksp.base"
version = libs.versions.hummus.get()

kotlin {
    jvmToolchain(21)
    jvm {

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":hummus"))
                implementation(libs.bundles.kotlin)
                implementation(libs.bundles.kotlinpoet)
                implementation(libs.kotlin.ksp)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


