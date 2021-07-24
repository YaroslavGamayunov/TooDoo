package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.*
import dagger.Binds
import dagger.Module

@Module(includes = [AppModule::class, CoroutineModule::class])
abstract class RepositoryModule {
    @ApplicationScoped
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository

    @Binds
    @LocalDataSource
    abstract fun bindLocalTaskDataSource(dataSource: DefaultLocalTaskDataSource): LocalTaskDataSource

    @Binds
    @RemoteDataSource
    abstract fun bindRemoteTaskDataSource(dataSource: RemoteTaskDataSource): TaskDataSource
}