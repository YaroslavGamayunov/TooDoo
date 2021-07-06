package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.domain.*
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module(includes = [RepositoryModule::class, CoroutineModule::class])
class UseCaseModule {
    @Provides
    fun provideAddTaskUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = AddTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideDeleteTaskUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = DeleteTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetTasksUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideCompleteTaskUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = CompleteTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetSingleTaskByIdUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetSingleTaskByIdUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetCountOfCompletedTasksUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetCountOfCompletedTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetCountOfDailyTasksUseCase(
        @IoDispatcher
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetCountOfDailyTasksUseCase(coroutineDispatcher, taskRepository)
}