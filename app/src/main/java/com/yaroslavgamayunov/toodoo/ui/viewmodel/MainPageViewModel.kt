package com.yaroslavgamayunov.toodoo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaroslavgamayunov.toodoo.di.ApplicationCoroutineScope
import com.yaroslavgamayunov.toodoo.domain.*
import com.yaroslavgamayunov.toodoo.domain.common.doIfError
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.domain.common.fold
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.withDifferentId
import com.yaroslavgamayunov.toodoo.exception.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainPageViewModel @Inject constructor(
    private val deleteTasksUseCase: DeleteTasksUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val synchronizeTasksUseCase: SynchronizeTasksUseCase,
    private val addTasksUseCase: AddTasksUseCase,
    getCountOfCompletedTasksUseCase: GetCountOfCompletedTasksUseCase,
    @ApplicationCoroutineScope
    private val externalScope: CoroutineScope,
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(listOf())
    val tasks = _tasks.asStateFlow()

    private val _failures = MutableSharedFlow<Failure>()
    val failures = _failures.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val completedTaskCount = getCountOfCompletedTasksUseCase(Unit)

    private val deletionUndoList = MutableStateFlow<List<Task>>(listOf())
    val deletionUndoCount = deletionUndoList.map { it.size }

    private var deletionUndoListCleaningJob: Job? = null
    private var taskCollectingJob: Job? = null

    // Ids of tasks completed during interaction with main page
    private val currentlyCompletedTaskIds = mutableSetOf<String>()

    var isShowingCompletedTasks = false
        set(value) {
            collectTasks(showingCompleted = value)
            field = value
        }

    private fun collectTasks(showingCompleted: Boolean) {
        taskCollectingJob?.cancel()
        taskCollectingJob = viewModelScope.launch {
            getTasksUseCase(
                GetTasksUseCaseParams(
                    showingCompleted,
                    currentlyCompletedTaskIds
                )
            ).collect { tasks ->
                tasks.fold(onSuccess = { _tasks.value = it }, onError = ::handleFailure)
            }
        }
    }

    init {
        collectTasks(isShowingCompletedTasks)
    }

    fun deleteTask(task: Task) {
        externalScope.launch {
            deleteTasksUseCase(listOf(task)).doIfSuccess {
                deletionUndoListCleaningJob?.cancel()
                deletionUndoListCleaningJob = viewModelScope.launch {
                    delay(TASK_DELETION_UNDO_TIMEOUT)
                    deletionUndoList.value = mutableListOf()
                }

                deletionUndoList.apply {
                    value = value.toMutableList().apply { add(task) }
                }
            }.doIfError(::handleFailure)
        }
    }

    fun undoDeletedTasks() {
        deletionUndoListCleaningJob?.cancel()
        viewModelScope.launch {
            addTasksUseCase.invoke(deletionUndoList.value.map { it.withDifferentId() })
            deletionUndoList.value = mutableListOf()
        }
    }

    fun completeTask(task: Task, isCompleted: Boolean) {
        if (isCompleted) {
            currentlyCompletedTaskIds.add(task.taskId)
        } else {
            currentlyCompletedTaskIds.remove(task.taskId)
        }

        externalScope.launch {
            completeTaskUseCase(task to isCompleted).doIfError(::handleFailure)
        }
    }

    fun refreshTasks() {
        externalScope.launch {
            _isRefreshing.value = true
            synchronizeTasksUseCase.invoke(Unit).doIfError(::handleFailure)
            _isRefreshing.value = false
        }
    }

    private fun handleFailure(failure: Failure) {
        viewModelScope.launch {
            _failures.emit(failure)
        }
    }

    companion object {
        const val TASK_DELETION_UNDO_TIMEOUT = 5000L
    }
}