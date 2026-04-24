plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
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
