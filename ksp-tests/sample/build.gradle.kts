plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
}

group = "com.github.klee0kai.hummus.ksp.base.sample"
version = libs.versions.hummus.get()

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":ksp-tests:processor"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

dependencies {
    ksp(project(":ksp-tests:processor"))
}

