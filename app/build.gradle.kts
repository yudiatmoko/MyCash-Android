plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.iyam.mycash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iyam.mycash"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    flavorDimensions += "env"
    productFlavors {
        create("production") {
            buildConfigField("String", "BASE_URL", "\"https://my-cash-mu.vercel.app/api/v1/\"")
        }
        create("integration") {
            buildConfigField("String", "BASE_URL", "\"https://my-cash-mu.vercel.app/api/v1/\"")
        }
    }
}

tasks.getByPath("preBuild").dependsOn("ktlintFormat")

ktlint {
    android.set(false)
    ignoreFailures.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.jupiter.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // coil
    implementation(libs.coil)
    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    // ktx lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // rv
    implementation(libs.androidx.recyclerview)
    // fragment ktx
    implementation(libs.androidx.fragment.ktx)
    // data store
    implementation(libs.androidx.datastore.preferences)
    // retrofit & okhttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    // chucker
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)
    // koin
    implementation(libs.koin.android)
    // unit testing
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.androidx.core.testing)
    // lottie
    implementation(libs.lottie)
    // image picker
    implementation(libs.imagepicker)
    // shimmer
    implementation(libs.shimmer)
    // serializable
    implementation(libs.kotlinx.serialization.json)
    // splashscreen
    implementation(libs.androidx.core.splashscreen)
    // swipe-to-refresh
    implementation(libs.androidx.swiperefreshlayout)
    // ticket view
    implementation(libs.ticketview)
    // qr-code
    implementation("com.github.androidmads:QRGenerator:1.0.5")
    implementation("com.google.zxing:core:3.3.2")
    // room db
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
}
