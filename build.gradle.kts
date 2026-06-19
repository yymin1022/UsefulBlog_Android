// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android / Kotlin plugin
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Hilt / KSP plugin
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false

    // GMS / Firebase plugin
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}