import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    jacoco
}

android {
    compileSdkVersion(Config.COMPILE_SDK)
    buildToolsVersion = Versions.BUILD_TOOLS

    defaultConfig {
        applicationId = "com.yaroslavgamayunov.toodoo"
        minSdkVersion(Config.MIN_SDK)
        targetSdkVersion(Config.TARGET_SDK)
        versionCode(Config.VERSION_CODE)
        versionName(Config.VERSION_NAME)

        multiDexEnabled = true
        testInstrumentationRunner = "com.yaroslavgamayunov.toodoo.TooDooAndroidTestRunner"

        buildConfigField(
            "String",
            "TASK_API_TOKEN",
            "\"${Config.TASK_API_TOKEN}\""
        )
    }

    buildTypes {
        getByName("debug") {
            isTestCoverageEnabled = true
        }

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
        languageVersion = "1.5"
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
    implementation(Dependencies.Android.SWIPE_REFRESH_LAYOUT)
    implementation(Dependencies.Android.FRAGMENT_KTX)

    implementation(Dependencies.Android.ROOM_RUNTIME)
    implementation(Dependencies.Android.ROOM_KTX)
    kapt(Dependencies.Android.ROOM_COMPILER)

    implementation(Dependencies.Android.NAVIGATION_UI_KTX)
    implementation(Dependencies.Android.NAVIGATION_FRAGMENT_KTX)

    implementation(Dependencies.Android.WORK)
    implementation(Dependencies.Android.RETROFIT)
    implementation(Dependencies.Android.LOGGING_INTERCEPTOR)
    implementation(Dependencies.Android.GSON)
    implementation(Dependencies.Android.GSON_CONVERTER)
    implementation(Dependencies.Android.TIMBER)

    // DI
    implementation(Dependencies.DI.DAGGER)
    kapt(Dependencies.DI.DAGGER_COMPILER)

    // Testing
    testImplementation(Dependencies.Testing.JUNIT)
    testImplementation(Dependencies.Testing.KOTEST_ASSERTIONS)
    testImplementation(Dependencies.Testing.COROUTINES_TEST)
    testImplementation(Dependencies.Testing.MOCKK)

    kaptAndroidTest(Dependencies.DI.DAGGER_COMPILER)
    androidTestImplementation(Dependencies.Testing.MOCKWEBSERVER)
    androidTestImplementation(Dependencies.Testing.ESPRESSO_CONTRIB)
    androidTestImplementation(Dependencies.Testing.JUNIT_EXT)
    androidTestImplementation(Dependencies.Testing.MOCKK_ANDROID)
    androidTestImplementation(Dependencies.Testing.ESPRESSO_CORE)

    // Core library desugaring
    coreLibraryDesugaring(Dependencies.CORE_LIBRARY_DESUGARING)
}

jacoco {
    toolVersion = Versions.JACOCO
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    val fileFilter = setOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$Lambda$*.*",
        "**/*\$inlined$*.*"
    )

    val classDirectoriesTree =
        fileTree(project.buildDir) {
            include(
                "**/classes/**/main/**",
                "**/intermediates/classes/debug/**",
                "**/intermediates/javac/debug/*/classes/**",
                "**/tmp/kotlin-classes/debug/**")

            exclude(fileFilter)
        }

    val sourceDirectoriesTree = fileTree(project.buildDir) {
        include(
            "src/main/java/**",
            "src/main/kotlin/**",
            "src/debug/java/**",
            "src/debug/kotlin/**"
        )
    }

    val executionDataTree = fileTree(project.buildDir) {
        include("outputs/code_coverage/**/*.ec",
            "jacoco/jacocoTestReportDebug.exec",
            "jacoco/testDebugUnitTest.exec",
            "jacoco/test.exec")
    }

    sourceDirectories.setFrom(sourceDirectoriesTree)
    classDirectories.setFrom(classDirectoriesTree)
    executionData.setFrom(executionDataTree)
}

tasks.withType(Test::class) {
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED
        )
        showStandardStreams = true
    }
}



tasks.register("runStaticChecks") {
    group = "Verify"
    description = "Runs static checks on the build"
    dependsOn("detekt")
}