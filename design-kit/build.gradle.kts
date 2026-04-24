plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

group = "com.github.klee0kai.hummus.compose"


kotlin {
    jvm()
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
        commonMain.dependencies {
            api(project(":hummus"))
            api(project(":design-core"))
            api(libs.bundles.kotlin)
            api(libs.bundles.compose)
        }

        commonTest.dependencies {
            api(libs.kotlin.test)
        }
        jvmTest.dependencies {
            api(libs.kotlinx.coroutines.test)
        }
        nativeTest.dependencies {
            api(libs.kotlinx.coroutines.test)
        }
        all {
            languageSettings {
                optIn("kotlin.ExperimentalStdlibApi")
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

dependencies {
    ksp(project(":design-ksp"))
}

