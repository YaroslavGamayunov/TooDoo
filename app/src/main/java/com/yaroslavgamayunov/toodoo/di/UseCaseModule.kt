package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.domain.AddTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.DeleteTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.GetAllTasksUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module(includes = [RepositoryModule::class, CoroutineModule::class])
class UseCaseModule {
    @Provides
    fun provideAddTaskUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = AddTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideDeleteTaskUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = DeleteTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetAllTasksUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetAllTasksUseCase(coroutineDispatcher, taskRepository)
}