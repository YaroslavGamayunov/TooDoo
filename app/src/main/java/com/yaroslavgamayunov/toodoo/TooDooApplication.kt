package com.yaroslavgamayunov.toodoo

import android.app.Application
import androidx.work.Configuration
import com.yaroslavgamayunov.toodoo.di.AppComponent
import com.yaroslavgamayunov.toodoo.di.DaggerAppComponent
import com.yaroslavgamayunov.toodoo.work.MorningNotificationWorker
import com.yaroslavgamayunov.toodoo.work.TaskSynchronizationWorker
import com.yaroslavgamayunov.toodoo.work.TooDooWorkerFactory
import timber.log.Timber
import javax.inject.Inject

interface AppComponentProvider {
    val appComponent: AppComponent
}

class TooDooApplication : Application(), Configuration.Provider, AppComponentProvider {
    override val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    @Inject
    lateinit var workerFactory: TooDooWorkerFactory

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        setupWorkers()
        setupTimber()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    private fun setupWorkers() {
        MorningNotificationWorker.schedule(this)
        TaskSynchronizationWorker.schedule(this)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
