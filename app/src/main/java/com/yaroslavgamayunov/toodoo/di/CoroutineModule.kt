package com.yaroslavgamayunov.toodoo.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
class CoroutineModule {
    @Provides
    @Named("mainDispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Named("ioDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}