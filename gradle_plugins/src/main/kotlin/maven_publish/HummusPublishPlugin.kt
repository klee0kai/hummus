package maven_publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class HummusPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(PublishingPlugin::class.java)

        project.afterEvaluate {
            project.extensions.configure<PublishingExtension> {
                publications.withType<MavenPublication> {
                    version = project.version as String
                    groupId = "com.github.klee0kai.hummus"
                    pom {
                        name.set("Hummus")
                        description.set("DevKit for multiplatform developing (JVM, JS, Native, WASM)")
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
                        scm {
                            url.set("https://github.com/klee0kai/hummus")
                            connection.set("scm:git:github.com/klee0kai/hummus.git")
                            developerConnection.set("scm:git:ssh://github.com/klee0kai/hummus.git")
                        }
                    }
                }
                repositories {
                    maven {
                        // https://docs.github.com/ru/actions/tutorials/publish-packages/publish-java-packages-with-maven
                        name = "GitHubPackages"
                        url = project.uri("https://maven.pkg.github.com/klee0kai/maven")
                        credentials {
                            username =
                                System.getenv("SECRETS_GH_ACTOR") ?: project.properties["github.actor"] as? String
                            password =
                                System.getenv("SECRETS_GH_API_TOKEN") ?: project.properties["github.token"] as? String
                        }
                    }
                }
            }
        }
    }

}

