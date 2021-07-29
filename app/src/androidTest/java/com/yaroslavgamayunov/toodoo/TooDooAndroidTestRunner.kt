package com.yaroslavgamayunov.toodoo

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TooDooAndroidTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application {
        return super.newApplication(cl, TooDooFakeApplication::class.java.name, context)
    }
}