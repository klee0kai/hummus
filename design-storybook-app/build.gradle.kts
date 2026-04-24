import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.github.klee0kai.hummus.storybook"
version = "1.0.0"
val entryClass = "${group}.DesignStorybookApp"

compose.desktop {
    application {
        mainClass = entryClass
        args("desktop")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "Design Storybook"
            packageVersion = "1.0.0"

            macOS {
                bundleID = project.group.toString()
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvm("desktop") {

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":design-kit"))
                implementation(project(":design-core"))
                implementation(project(":design-storybook"))
                implementation(project(path = ":design-storybook", configuration = "wasmArchives"))

                implementation(libs.picocli)
                implementation(libs.bundles.compose)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.netty)
                implementation(libs.jmdns)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Assembles a fat JAR with all dependencies"
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = entryClass
    }
    val desktopMain = kotlin.targets.named("desktop").flatMap {
        (it as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget)
            .compilations.named("main")
            .map { compilation -> compilation.runtimeDependencyFiles }
    }
    val desktopJar = tasks.named("desktopJar", Jar::class)
    dependsOn(desktopJar)
    from(desktopJar.map { zipTree(it.archiveFile) })
    from(desktopMain.map { files -> files.map { if (it.isDirectory) it else zipTree(it) } })
    from(configurations.getByName("desktopRuntimeClasspath").map {
        if (it.isDirectory) it else zipTree(it)
    })
    isZip64 = true
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}
