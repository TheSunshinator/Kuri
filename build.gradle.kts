import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.android.library")
    id("maven-publish")
}

group = "com.nuglif"
version = "0.2.0"

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()

    targets {
        jvm()

        js(BOTH) {
            browser()
            nodejs()
        }

        android()

        linuxX64()
        mingwX64()
        macosX64()

        tvos()

        watchosArm32()
        watchosArm64()
        watchosX86()
        watchosX64()

        iosX64()
        iosArm64()
        iosArm32()
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

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        jvmTarget = "1.8"
        apiVersion = "1.6"
        languageVersion = "1.6"
    }
}
