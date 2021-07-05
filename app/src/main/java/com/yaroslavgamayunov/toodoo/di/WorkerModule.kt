package com.yaroslavgamayunov.toodoo.di

import androidx.work.WorkerFactory
import com.yaroslavgamayunov.toodoo.work.MorningNotificationWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module(includes = [AppModule::class])
abstract class WorkerModule {
    @Binds
    @IntoSet
    abstract fun bindMorningNotificationWorker(factory: MorningNotificationWorkerFactory): WorkerFactory
}