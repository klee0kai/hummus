plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.publish.stone)
}

group = "com.github.klee0kai.hummus"


kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()
    mingwX64()
    wasmJs {
        browser()
        nodejs()
    }

    // Disable yarn lock file validation
    rootProject.tasks.matching { it.name == "kotlinStoreYarnLock" }.all {
        enabled = false
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.kotlin)
            api(libs.stone.ref)
            api(libs.stone.inject)
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

