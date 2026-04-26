pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/klee0kai/maven")
            credentials {
                username = System.getenv("SECRETS_GH_ACTOR")
                    ?: settings.providers.gradleProperty("github.actor").orNull

                password = System.getenv("SECRETS_GH_API_TOKEN")
                    ?: settings.providers.gradleProperty("github.token").orNull
            }
        }
        maven(url = "https://jitpack.io")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/klee0kai/maven")
            credentials {
                username = System.getenv("SECRETS_GH_ACTOR")
                    ?: settings.providers.gradleProperty("github.actor").orNull

                password = System.getenv("SECRETS_GH_API_TOKEN")
                    ?: settings.providers.gradleProperty("github.token").orNull
            }
        }
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
    ":design-storybook-app",
)
