import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

group = "com.github.klee0kai.hummus.storybook"
version = libs.versions.hummus.get()

kotlin {
    jvmToolchain(21)
    jvm("desktop") {

    }
    js(IR) {
        outputModuleName = "composeAppJs"
        browser {
            commonWebpackConfig {
                outputFileName = "bundle.js"
            }
        }
        binaries.executable()
    }

    wasmJs {
        outputModuleName = "composeAppWasm"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "bundle.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    cssSupport { enabled = true }
                    mode = KotlinWebpackConfig.Mode.DEVELOPMENT
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }

        binaries.executable()
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

tasks.named("sourcesJar") {
    dependsOn("kspCommonMainKotlinMetadata")
}


val wasmArtifactsJar by tasks.register<Jar>(name = "wasmJsBrowserProductionJar") {
    group = "build"
    description = "Package WebAssembly production artifacts into a JAR"

    archiveBaseName.set("wasm-artifacts")
    archiveClassifier.set("prod")
    archiveVersion.set(libs.versions.hummus.get())
    archiveAppendix.set("wasm-artifacts")

    dependsOn("wasmJsBrowserDistribution")

    from(layout.buildDirectory.dir("dist/wasmJs/productionExecutable")) {
        include("**/*")
    }
}

val wasmArchives by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}

artifacts {
    add("wasmArchives", wasmArtifactsJar)
}

dependencies {
    kspCommonMainMetadata(libs.stone.ksp)
    ksp(project(":design-ksp"))
}
