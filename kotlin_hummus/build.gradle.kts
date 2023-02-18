plugins {
    id("kotlin")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}