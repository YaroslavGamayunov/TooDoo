package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.TaskEntityMapper
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class GetAllTasksUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Unit, List<Task>>(dispatcher) {
    override suspend fun execute(params: Unit): List<Task> {
        return taskRepository.getAllTasks().map { TaskEntityMapper.toTask(it) }
    }
}