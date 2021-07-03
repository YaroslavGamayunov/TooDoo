// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
        classpath("com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id(Dependencies.Testing.DETEKT) version Versions.DETEKT
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin(Dependencies.Testing.DETEKT)
    }

    detekt {
        buildUponDefaultConfig = true
        autoCorrect = true
        config = rootProject.files("config/detekt/config.yml")
        baseline = rootProject.file("config/detekt/baseline.xml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}