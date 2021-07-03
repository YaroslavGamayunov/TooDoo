package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.GetCountOfCompletedTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.*
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.notifyObservers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPageViewModel @Inject constructor(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val addTasksUseCase: AddTasksUseCase,
    getCountOfCompletedTasksUseCase: GetCountOfCompletedTasksUseCase
) : ViewModel() {
    private val deletionUndoList = MutableStateFlow<MutableList<Task>>(mutableListOf())
    val deletionUndoCount = deletionUndoList.map { it.size }

    private var deletionUndoListCleaningJob: Job? = null
    private var taskCollectingJob: Job? = null

    // Ids of tasks completed during interaction with main page
    private val currentlyCompletedTaskIds = mutableSetOf<Int>()

    var isShowingCompletedTasks = false
        set(value) {
            collectTasks(showingCompleted = value)
            field = value
        }

    private val _tasks = MutableStateFlow<Result<List<Task>>>(Result.Loading)
    val tasks = _tasks.asStateFlow()

    val completedTaskCount = getCountOfCompletedTasksUseCase(Unit)

    private fun collectTasks(showingCompleted: Boolean) {
        taskCollectingJob?.cancel()
        taskCollectingJob = viewModelScope.launch(Dispatchers.IO) {
            getTasksUseCase(
                GetTasksUseCaseParams(
                    showingCompleted,
                    currentlyCompletedTaskIds
                )
            ).collect { _tasks.value = it }
        }
    }

    init {
        collectTasks(isShowingCompletedTasks)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task).doIfSuccess {
                deletionUndoListCleaningJob?.cancel()
                deletionUndoListCleaningJob = viewModelScope.launch {
                    delay(TASK_DELETION_UNDO_TIMEOUT)
                    deletionUndoList.value = mutableListOf()
                }

                deletionUndoList.apply {
                    value.add(task)
                    notifyObservers()
                }
            }
        }
    }

    fun undoDeletedTasks() {
        deletionUndoListCleaningJob?.cancel()
        viewModelScope.launch {
            addTasksUseCase(deletionUndoList.value)
            deletionUndoList.value = mutableListOf()
        }
    }

    fun completeTask(task: Task, isCompleted: Boolean) {
        if (isCompleted) {
            currentlyCompletedTaskIds.add(task.taskId)
        } else {
            currentlyCompletedTaskIds.remove(task.taskId)
        }

        viewModelScope.launch {
            completeTaskUseCase(
                task to isCompleted
            )
        }
    }

    companion object {
        const val TASK_DELETION_UNDO_TIMEOUT = 5000L
    }
}