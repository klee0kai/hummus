plugins {
    alias(libs.plugins.kotlin.jvm)
//    alias(libs.plugins.publish.maven)
//    alias(libs.plugins.publish.crossbox)
}

group = "com.github.klee0kai.crossbox.processor"
version = libs.versions.hummus.get()

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.kotlinpoet)
    implementation(libs.ksp)
}