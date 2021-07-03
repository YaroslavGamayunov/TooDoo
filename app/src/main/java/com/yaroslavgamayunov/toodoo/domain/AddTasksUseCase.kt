package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskEntity
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddTasksUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<List<Task>, Unit>(dispatcher) {
    override suspend fun execute(params: List<Task>) {
        taskRepository.insertTasks(params.map { it.toTaskEntity() })
    }
}