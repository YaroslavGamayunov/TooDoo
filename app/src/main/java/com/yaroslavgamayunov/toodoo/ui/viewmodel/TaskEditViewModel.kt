package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.domain.AddTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.DeleteTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.GetSingleTaskByIdUseCase
import com.yaroslavgamayunov.toodoo.domain.UpdateTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class TaskEditViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getSingleTaskByIdUseCase: GetSingleTaskByIdUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private var _editableTask = MutableStateFlow(
        Task(
            taskId = UUID.randomUUID().toString(),
            description = "",
            isCompleted = false,
            deadline = TimeUtils.maxZonedDateTime,
            scheduleMode = TaskScheduleMode.Unspecified,
            priority = TaskPriority.None
        )
    )

    val task get() = _editableTask.asStateFlow()
    private var isNewTaskCreated = true

    fun loadTaskForEditing(id: String) {
        viewModelScope.launch {
            getSingleTaskByIdUseCase(id).doIfSuccess {
                isNewTaskCreated = false
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
        viewModelScope.launch {
            if (isNewTaskCreated) {
                addTaskUseCase(_editableTask.value)
            } else {
                updateTaskUseCase(_editableTask.value)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(_editableTask.value)
        }
    }
}