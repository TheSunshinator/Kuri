plugins {
    `library-conventions`
    `publishing-conventions`
}

kotlin.sourceSets.getByName("commonTest").dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.bundles.kotest.assertions)
}
