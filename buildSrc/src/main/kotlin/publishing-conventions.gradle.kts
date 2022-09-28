import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.util.*

plugins {
    `maven-publish`
    signing
}

group = "com.sunshinator"
version = "0.2.0"

val localProperties = project.rootProject.file("local.properties")
    .takeIf { it.exists() }
    ?.reader()
    ?.use { reader ->
        Properties().apply { load(reader) }
    }

extra["ossrhUsername"] = localProperties?.get("ossrhUsername")?.toString() ?: System.getenv("OSSRH_USERNAME")
extra["ossrhPassword"] = localProperties?.get("ossrhPassword")?.toString() ?: System.getenv("OSSRH_PASSWORD")
extra["signing.keyId"] = localProperties?.get("signing.keyId")?.toString() ?: System.getenv("SIGNING_KEY_ID")
extra["signing.password"] = localProperties?.get("signing.password")?.toString() ?: System.getenv("SIGNING_PASSWORD")
extra["signing.secretKeyRingFile"] = localProperties?.get("signing.secretKeyRingFile")?.toString()
    ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE")

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "Deploy"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = extra["ossrhUsername"]?.toString()
                password = extra["ossrhPassword"]?.toString()
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set("Kuri")
            description.set("Multiplatform library for URI manipulation")
            url.set("https://github.com/TheSunshinator/Kuri")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("The Sunshinator")
                    name.set("Luc-Antoine Girardin")
                    email.set("the.sunshinator@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/TheSunshinator/Kuri")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}