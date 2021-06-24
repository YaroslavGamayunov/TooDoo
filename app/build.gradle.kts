plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android")
}

android {
    compileSdkVersion(Versions.COMPILE_SDK)
    buildToolsVersion = Versions.BUILD_TOOLS

    defaultConfig {
        applicationId = "com.yaroslavgamayunov.toodoo"
        minSdkVersion(Versions.MIN_SDK)
        targetSdkVersion(Versions.TARGET_SDK)
        versionCode(Versions.VERSION_CODE)
        versionName(Versions.VERSION_NAME)

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Kotlin
    implementation(Dependencies.Kotlin.STDLIB)
    implementation(Dependencies.Kotlin.CORE_KTX)
    implementation(Dependencies.Kotlin.COROUTINES)

    // Android
    implementation(Dependencies.Android.APPCOMPAT)
    implementation(Dependencies.Android.MATERIAL)
    implementation(Dependencies.Android.CONSTRAINT_LAYOUT)
    implementation(Dependencies.Android.COORDINATOR_LAYOUT)
    implementation(Dependencies.Android.FRAGMENT_KTX)

    implementation(Dependencies.Android.ROOM_RUNTIME)
    implementation(Dependencies.Android.ROOM_KTX)
    kapt(Dependencies.Android.ROOM_COMPILER)

    implementation(Dependencies.Android.NAVIGATION_UI_KTX)
    implementation(Dependencies.Android.NAVIGATION_FRAGMENT_KTX)

    // DI
    implementation(Dependencies.DI.DAGGER)
    kapt(Dependencies.DI.DAGGER_COMPILER)

    // Testing
    testImplementation(Dependencies.Testing.JUNIT)
    androidTestImplementation(Dependencies.Testing.JUNIT_EXT)
    androidTestImplementation(Dependencies.Testing.ESPRESSO_CORE)

    // Core library desugaring
    coreLibraryDesugaring(Dependencies.CORE_LIBRARY_DESUGARING)
}

tasks.register("checkStatic") {
    group = "Verify"
    description = "Runs static checks on the build"
    dependsOn("detekt")
}