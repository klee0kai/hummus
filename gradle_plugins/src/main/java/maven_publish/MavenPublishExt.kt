package maven_publish

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get


fun PublishingExtension.hummusToMaven(project: Project) {
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

            pom {
                name.set("Hummus")
                description.set("DevKit for android/java/kotlin developing")
                url.set("https://github.com/klee0kai/hummus")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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