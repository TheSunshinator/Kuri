plugins {
    kotlin("multiplatform") version "1.5.10"
    id("com.android.library")
    id("maven-publish")
}

group = "com.nuglif"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()

    android {
        publishLibraryVariants("release", "debug")
    }

    js(BOTH) {
        browser()
        nodejs()
    }

    jvm {
        compilations.all { kotlinOptions.jvmTarget = "1.8" }
        testRuns["test"].executionTask.configure { useJUnitPlatform() }
    }

    iosX64("ios").binaries.framework {
        baseName = rootProject.name
    }

    val hostOs = System.getProperty("os.name")
    when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        getByName("commonTest").dependencies {
            implementation(kotlin("test"))
            implementation("io.kotest:kotest-assertions-core:5.0.3")
        }
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(31)
    }
}
