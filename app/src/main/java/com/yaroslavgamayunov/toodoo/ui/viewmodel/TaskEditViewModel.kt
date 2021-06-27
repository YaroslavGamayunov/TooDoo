package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.domain.*
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class TaskEditViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val getSingleTaskByIdUseCase: GetSingleTaskByIdUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private var _editableTask = MutableStateFlow(
        Task(
            taskId = 0,
            description = "",
            isCompleted = false,
            deadline = Instant.MAX,
            scheduleMode = TaskScheduleMode.Unspecified,
            priority = TaskPriority.None
        )
    )

    val task get() = _editableTask.asStateFlow()

    suspend fun getTask(id: Int): Result<Task> {
        val result = getSingleTaskByIdUseCase(id)
        if (result is Result.Success) {
            _editableTask.value = result.data
        }
        return result
    }

    fun updateDescription(description: String) {
        _editableTask.value = _editableTask.value.copy(description = description)
    }

    fun updatePriority(priority: TaskPriority) {
        _editableTask.value = _editableTask.value.copy(priority = priority)
    }

    fun updateDeadline(deadline: Instant) {
        _editableTask.value = _editableTask.value.copy(deadline = deadline)
    }

    fun updateScheduleMode(scheduleMode: TaskScheduleMode) {
        _editableTask.value = _editableTask.value.copy(scheduleMode = scheduleMode)
    }

    suspend fun saveChanges() {
        addTaskUseCase(_editableTask.value)
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(_editableTask.value)
        }
    }
}