plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}


gradlePlugin {
    plugins {
        create("hummusPublish") {
            id = "hummus.publish.maven"
            implementationClass = "maven_publish.HummusPublishPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}
