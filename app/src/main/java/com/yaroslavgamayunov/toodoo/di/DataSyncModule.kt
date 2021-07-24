package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.datasync.DefaultTaskSyncStrategy
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncStrategy
import dagger.Module
import dagger.Provides

@Module
class DataSyncModule {
    @Provides
    fun provideTaskSyncStrategy(): TaskSyncStrategy = DefaultTaskSyncStrategy
}