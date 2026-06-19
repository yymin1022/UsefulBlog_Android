plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Project dependency
    implementation(project(":domain"))

    // Gson dependency
    implementation(libs.converter.gson)

    // Retrofit dependency
    implementation(libs.retrofit)
}