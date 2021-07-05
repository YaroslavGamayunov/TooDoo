package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.domain.GetCountOfCompletedTasksUseCase
import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.domain.*
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

@Module(includes = [RepositoryModule::class, CoroutineModule::class])
class UseCaseModule {
    @Provides
    fun provideAddTaskUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = AddTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideDeleteTaskUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = DeleteTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetTasksUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideCompleteTaskUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = CompleteTaskUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetSingleTaskByIdUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetSingleTaskByIdUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetCountOfCompletedTasksUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetCountOfCompletedTasksUseCase(coroutineDispatcher, taskRepository)

    @Provides
    fun provideGetCountOfDailyTasksUseCase(
        @Named("ioDispatcher")
        coroutineDispatcher: CoroutineDispatcher,
        taskRepository: TaskRepository
    ) = GetCountOfDailyTasksUseCase(coroutineDispatcher, taskRepository)
}