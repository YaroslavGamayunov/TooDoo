package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yaroslavgamayunov.toodoo.GetNumberOfCompletedTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.*
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainPageViewModel @Inject constructor(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val getNumberOfCompletedTasksUseCase: GetNumberOfCompletedTasksUseCase
) : ViewModel() {
    // Ids of tasks completed during interaction with main page
    private val currentlyCompletedTaskIds = mutableSetOf<Int>()

    suspend fun tasks(isShowingCompletedTasks: Boolean): Result<Flow<List<Task>>> =
        getTasksUseCase(GetTasksUseCaseParams(isShowingCompletedTasks, currentlyCompletedTaskIds))

    suspend fun deleteTask(task: Task): Result<Unit> = deleteTaskUseCase(task)

    suspend fun completeTask(task: Task, isCompleted: Boolean): Result<Unit> {
        if (isCompleted) {
            currentlyCompletedTaskIds.add(task.taskId)
        } else {
            currentlyCompletedTaskIds.remove(task.taskId)
        }
        return completeTaskUseCase(
            task to isCompleted
        )
    }

    suspend fun getNumberOfCompletedTasks() = getNumberOfCompletedTasksUseCase(Unit)
}