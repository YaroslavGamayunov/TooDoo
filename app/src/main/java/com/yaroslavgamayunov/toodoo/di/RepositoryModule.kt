package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.DefaultTaskRepository
import com.yaroslavgamayunov.toodoo.data.TaskRepository
import dagger.Binds
import dagger.Module

@Module(includes = [AppModule::class, CoroutineModule::class])
abstract class RepositoryModule {
    @ApplicationScoped
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository
}