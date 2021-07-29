package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.AddTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.DeleteTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.GetSingleTaskByIdUseCase
import com.yaroslavgamayunov.toodoo.domain.UpdateTaskUseCase
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class TaskEditViewModel @Inject constructor(
    private val addTasksUseCase: AddTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getSingleTaskByIdUseCase: GetSingleTaskByIdUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : ViewModel() {

    private var editableTask = MutableStateFlow(
        Task(
            taskId = UUID.randomUUID().toString(),
            description = "",
            isCompleted = false,
            deadline = TimeUtils.maxZonedDateTime,
            priority = TaskPriority.None
        )
    )

    val task get() = editableTask.asStateFlow()
    private var isNewTaskCreated = true

    fun loadTaskForEditing(id: String) {
        viewModelScope.launch {
            getSingleTaskByIdUseCase(id).doIfSuccess {
                isNewTaskCreated = false
                editableTask.value = it
            }
        }
    }

    fun updateDescription(description: String) {
        editableTask.value = editableTask.value.copy(description = description)
    }

    fun updatePriority(priority: TaskPriority) {
        editableTask.value = editableTask.value.copy(priority = priority)
    }

    fun updateDeadline(deadline: ZonedDateTime) {
        editableTask.value = editableTask.value.copy(deadline = deadline)
    }

    fun disableDeadline() {
        editableTask.value = editableTask.value.copy(deadline = TimeUtils.maxZonedDateTime)
    }

    fun saveChanges() {
        viewModelScope.launch {
            if (isNewTaskCreated) {
                addTasksUseCase(listOf(editableTask.value))
            } else {
                updateTaskUseCase(editableTask.value)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTasksUseCase(listOf(editableTask.value))
        }
    }
}