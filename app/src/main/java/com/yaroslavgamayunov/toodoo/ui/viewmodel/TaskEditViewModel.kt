package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.domain.AddTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.DeleteTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.GetSingleTaskByIdUseCase
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

class TaskEditViewModel @Inject constructor(
    private val addTasksUseCase: AddTasksUseCase,
    private val getSingleTaskByIdUseCase: GetSingleTaskByIdUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private var _editableTask = MutableStateFlow(
        Task(
            taskId = 0,
            description = "",
            isCompleted = false,
            deadline = TimeUtils.maxZonedDateTime,
            scheduleMode = TaskScheduleMode.Unspecified,
            priority = TaskPriority.None
        )
    )

    val task get() = _editableTask.asStateFlow()

    fun loadTaskForEditing(id: Int) {
        viewModelScope.launch {
            getSingleTaskByIdUseCase(id).doIfSuccess {
                _editableTask.value = it
            }
        }
    }

    fun updateDescription(description: String) {
        _editableTask.value = _editableTask.value.copy(description = description)
    }

    fun updatePriority(priority: TaskPriority) {
        _editableTask.value = _editableTask.value.copy(priority = priority)
    }

    fun updateDeadline(deadline: ZonedDateTime) {
        _editableTask.value = _editableTask.value.copy(deadline = deadline)
    }

    fun updateScheduleMode(scheduleMode: TaskScheduleMode) {
        _editableTask.let {
            it.value = if (scheduleMode == TaskScheduleMode.Unspecified) it.value.copy(
                scheduleMode = scheduleMode,
                deadline = TimeUtils.maxZonedDateTime
            ) else it.value.copy(scheduleMode = scheduleMode)
        }
    }

    fun saveChanges() {
        viewModelScope.launch { addTasksUseCase(listOf(_editableTask.value)) }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(_editableTask.value)
        }
    }
}