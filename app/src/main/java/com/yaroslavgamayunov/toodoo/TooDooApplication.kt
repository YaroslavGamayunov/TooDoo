package com.yaroslavgamayunov.toodoo

import android.app.Application
import androidx.work.Configuration
import com.yaroslavgamayunov.toodoo.di.AppComponent
import com.yaroslavgamayunov.toodoo.di.DaggerAppComponent
import com.yaroslavgamayunov.toodoo.work.MorningNotificationWorker
import com.yaroslavgamayunov.toodoo.work.TooDooWorkerFactory
import javax.inject.Inject

class TooDooApplication : Application(), Configuration.Provider {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    @Inject
    lateinit var workerFactory: TooDooWorkerFactory

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        setupWorkers()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    private fun setupWorkers() {
        MorningNotificationWorker.schedule(this)
    }
}
