package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.FlowUseCase
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class GetTasksUseCaseParams(
    val showCompletedTasks: Boolean,
    val currentlyCompletedTaskIds: Set<String>
)

private typealias TaskListMapper = suspend (List<Task>) -> (List<Task>)

class GetTasksUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : FlowUseCase<GetTasksUseCaseParams, List<Task>>(dispatcher) {
    override fun execute(params: GetTasksUseCaseParams): Flow<Result<List<Task>>> {
        return taskRepository.getAllTasks()
            .map(removeNotNeeded(params))
            .map { Result.Success(it) }
    }

    private fun removeNotNeeded(params: GetTasksUseCaseParams): TaskListMapper {
        return { list -> list.filter { isNeededToBeShown(it, params) } }
    }

    private fun isNeededToBeShown(task: Task, params: GetTasksUseCaseParams): Boolean {
        return params.showCompletedTasks ||
                (!task.isCompleted || task.taskId in params.currentlyCompletedTaskIds)
    }
}