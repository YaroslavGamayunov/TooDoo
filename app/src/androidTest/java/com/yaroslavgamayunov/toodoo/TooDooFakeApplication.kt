package com.yaroslavgamayunov.toodoo

import android.app.Application
import android.os.StrictMode
import com.yaroslavgamayunov.toodoo.di.AppComponent
import com.yaroslavgamayunov.toodoo.di.DaggerTestAppComponent


class TooDooFakeApplication : Application(), AppComponentProvider {
    override val appComponent: AppComponent by lazy {
        DaggerTestAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Doing this to access network on main thread for initializing MockWebServer, it is ok in tests, isn't it?:)
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}