package com.yaroslavgamayunov.toodoo.di

import android.util.Log
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

@Module
class CoroutineModule {
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @ApplicationScoped
    @ApplicationCoroutineScope
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            Timber.d(throwable)
            if (throwable is HttpException || throwable is UnknownHostException) {
                Timber.d(throwable)
            } else {
                throw throwable
            }
        })
}