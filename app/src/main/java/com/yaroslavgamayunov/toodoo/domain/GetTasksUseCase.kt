package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class GetTasksUseCaseParams(
    val showCompletedTasks: Boolean,
    val currentlyCompletedTaskIds: Set<Int>
)

class GetTasksUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<GetTasksUseCaseParams, Flow<List<Task>>>(dispatcher) {
    override suspend fun execute(params: GetTasksUseCaseParams): Flow<List<Task>> {
        return taskRepository.getAllTasks()
            .map(mapTaskFlow(params))
    }

    private fun mapTaskFlow(params: GetTasksUseCaseParams): suspend (List<TaskEntity>) -> (List<Task>) {
        return { list ->
            list
                .map {
                    it.toTask()
                }
                .filter { task ->
                    params.showCompletedTasks || (!task.isCompleted || task.taskId in params.currentlyCompletedTaskIds)
                }
        }
    }
}