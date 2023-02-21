plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


gradlePlugin {
    plugins.register("hummus_publish") {
        id = "maven_publish.hummus_publish"
        implementationClass = "maven_publish.HummusPublishPlugin"
    }
}
