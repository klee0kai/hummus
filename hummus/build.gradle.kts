plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
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

    sourceSets {
        commonMain.dependencies {
//            api(libs.java.inject)
//            api(libs.kotlinx.coroutines)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        all {
            languageSettings {
                // Enables expect/actual classes support without warnings
                optIn("kotlin.ExperimentalStdlibApi")
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

