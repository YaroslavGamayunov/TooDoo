package com.yaroslavgamayunov.toodoo.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

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
    fun provideApplicationCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}