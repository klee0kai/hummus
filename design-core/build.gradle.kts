plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.publish.hummus.maven)
}

group = "com.github.klee0kai.hummus.design.core"
version = libs.versions.hummus.get()


kotlin {
    jvmToolchain(21)
    jvm {

    }
    js(IR) {
        browser()
        nodejs()
    }

    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {
                api(libs.stone.kotlin)
                api(libs.bundles.kotlin)
                api(libs.bundles.compose)
            }
        }

    }
}

