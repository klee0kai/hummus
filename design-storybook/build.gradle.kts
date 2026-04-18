plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish.maven)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.publish.stone)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

group = "com.github.klee0kai.hummus.storybook"


kotlin {
    jvm("desktop") {

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
                api(project(":design-kit"))
                api(libs.stone.kotlin)
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.desktop.currentOs)
                api(libs.kotlinx.coroutines.swing)
                api(libs.compose.tooling)
            }
        }

        val commonTest by getting {
            dependencies {

            }
        }

    }
}

tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
//    enabled = false
}


dependencies {
    kspCommonMainMetadata(libs.stone.ksp)
    ksp(project(":design-ksp"))
}
