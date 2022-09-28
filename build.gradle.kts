plugins {
    `version-catalog`
    `maven-publish`
    `publishing-conventions`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
}
