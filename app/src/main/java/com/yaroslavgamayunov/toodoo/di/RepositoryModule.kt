package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.DefaultTaskRepository
import com.yaroslavgamayunov.toodoo.data.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AppModule::class])
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository
}