package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yaroslavgamayunov.toodoo.domain.AddTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.DeleteTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.GetAllTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.TaskParameters
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import javax.inject.Inject

class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase
) : ViewModel() {
    suspend fun tasks(): Result<List<Task>> = getAllTasksUseCase(Unit)
    suspend fun addTask(taskParameters: TaskParameters): Result<Unit> =
        addTaskUseCase(taskParameters)

    suspend fun deleteTask(task: Task): Result<Unit> = deleteTaskUseCase(task)
}