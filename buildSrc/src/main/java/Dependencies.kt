object Versions {
    const val ANDROID_GRADLE_PLUGIN = "4.2.1"
    const val BUILD_TOOLS = "30.0.3"

    const val KOTLIN = "1.5.10"
    const val CORE_KTX = "1.5.0"
    const val COROUTINES = "1.4.3"

    const val APPCOMPAT = "1.3.0"
    const val MATERIAL = "1.3.0"
    const val CONSTRAINT_LAYOUT = "2.0.4"
    const val COORDINATOR_LAYOUT = "1.1.0"
    const val SWIPE_REFRESH_LAYOUT = "1.1.0"
    const val FRAGMENT = "1.3.4"
    const val NAVIGATION = "2.3.5"
    const val WORK = "2.5.0"

    const val RETROFIT = "2.9.0"
    const val LOGGING_INTERCEPTOR = "4.9.1"
    const val GSON = "2.8.7"
    const val TIMBER = "4.7.1"

    const val ROOM = "2.3.0"
    const val DAGGER = "2.36"
    const val CORE_LIBRARY_DESUGARING = "1.1.5"

    const val JUNIT = "4.13.2"
    const val JUNIT_EXT = "1.1.2"
    const val ESPRESSO_CORE = "3.3.0"

    const val DETEKT = "1.17.1"
    const val KOTEST = "4.6.1"
}

object Dependencies {
    object Kotlin {
        const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
        const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
        const val COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    }

    object Android {
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
        const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
        const val CONSTRAINT_LAYOUT =
            "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
        const val COORDINATOR_LAYOUT =
            "androidx.coordinatorlayout:coordinatorlayout:${Versions.COORDINATOR_LAYOUT}"
        const val SWIPE_REFRESH_LAYOUT =
            "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.SWIPE_REFRESH_LAYOUT}"
        const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"

        const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
        const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
        const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"

        const val NAVIGATION_FRAGMENT_KTX =
            "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
        const val NAVIGATION_UI_KTX =
            "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"

        const val WORK = "androidx.work:work-runtime-ktx:${Versions.WORK}"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
        const val LOGGING_INTERCEPTOR =
            "com.squareup.okhttp3:logging-interceptor:${Versions.LOGGING_INTERCEPTOR}"
        const val GSON = "com.google.code.gson:gson:${Versions.GSON}"
        const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}"
        const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
    }

    object DI {
        const val DAGGER = "com.google.dagger:dagger:${Versions.DAGGER}"
        const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER}"
    }

    object Testing {
        const val JUNIT = "junit:junit:${Versions.JUNIT}"
        const val JUNIT_EXT = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
        const val DETEKT = "io.gitlab.arturbosch.detekt"
        const val KOTEST_ASSERTIONS = "io.kotest:kotest-assertions-core:${Versions.KOTEST}"
    }

    const val CORE_LIBRARY_DESUGARING =
        "com.android.tools:desugar_jdk_libs:${Versions.CORE_LIBRARY_DESUGARING}"
}
