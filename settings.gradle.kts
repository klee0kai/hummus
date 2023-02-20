pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Hummus"
includeBuild("gradle_plugins")
include(":kotlin_hummus")
include(":java_hummus")
include(":android_java_hummus")
include(":android_kotlin_hummus")
