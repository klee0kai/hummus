plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

group = "com.github.klee0kai.hummus.design.core"


kotlin {
    jvm() {

    }
    js(IR) {
        browser()
        nodejs()
    }

//    linuxX64()
//    mingwX64()
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

