pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "Hummus"
includeBuild("gradle_plugins")
include(
    ":hummus",
    ":ksp-base",
    ":ksp-tests:processor",
    ":ksp-tests:sample",
    ":design-core",
    ":design-kit",
    ":design-ksp",
    ":design-storybook",
)
