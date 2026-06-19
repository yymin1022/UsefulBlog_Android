plugins {
    // Android / Kotlin plugin
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // Firebase / GMS plugin
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)

    // Hilt / KSP plugin
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.yong.blog"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.yong.blog"
        minSdk = 24
        targetSdk = 37
        versionCode = 20100
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Project dependency
    implementation(project(":data"))
    implementation(project(":domain"))

    // Android dependency
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(platform(libs.androidx.compose.bom))

    // Hilt dependency
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Kotlin dependency
    ksp(libs.kotlin.metadata.jvm)

    // Firebase / GMS dependency
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(platform(libs.firebase.bom))

    // Coil dependency
    implementation(libs.coil.compose)

    // Markwon dependency
    implementation(libs.markwon.core)
    implementation(libs.markwon.ext.strikethrough)
    implementation(libs.markwon.ext.tables)
    implementation(libs.markwon.html)
    implementation(libs.markwon.image)
    implementation(libs.markwon.linkify)
    implementation(libs.markwon.syntax.highlight) {
        // prism4j 2.0.0이 끌어오는 구형 annotations-java5가
        // 현대 org.jetbrains:annotations와 중복 클래스를 일으켜 제외
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation(libs.prism4j.languages)

    // Okhttp dependency
    implementation(libs.okhttp)

    // Test dependency
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug dependency
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
