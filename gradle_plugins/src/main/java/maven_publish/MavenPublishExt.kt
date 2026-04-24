package maven_publish

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register


fun PublishingExtension.hummusToMaven(project: Project) {
    repositories {
        mavenLocal()
    }

    // Create sources jar
    val sourcesJar = project.tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(project.fileTree("src") {
            include("**/kotlin/**/*.kt")
        })
    }

    // Create dokka javadoc jar
    val dokkaJavadocJar = project.tasks.register<Jar>("dokkaJavadocJar") {
        archiveClassifier.set("javadoc")
        from(project.tasks.findByName("dokkaHtml"))
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            when {
                "java" in project.components.names -> {
                    //publish simple java lib
                    from(project.components["java"])
                }
                "release" in project.components.names -> {
                    //publish android lib
                    from(project.components["release"])
                }
            }

            // Add sources and javadoc artifacts
            artifact(sourcesJar)
            artifact(dokkaJavadocJar)

            pom {
                name.set("Hummus")
                description.set("DevKit for android/java/kotlin developing")
                url.set("https://github.com/klee0kai/hummus")
                licenses {
                    license {
                        name.set("GNU General Public License, Version 3")
                        url.set("https://github.com/klee0kai/hummus/blob/dev/LICENCE.md")
                    }
                }
                developers {
                    developer {
                        id.set("klee0kai")
                        name.set("Andrey Kuzubov")
                        email.set("klee0kai@gmail.com")
                    }
                }
            }
        }
    }
}