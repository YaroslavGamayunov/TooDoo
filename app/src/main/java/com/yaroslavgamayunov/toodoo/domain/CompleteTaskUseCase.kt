package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskEntity
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Pair<Task, Boolean>, Unit>(dispatcher) {
    override suspend fun execute(params: Pair<Task, Boolean>) {
        val (task, completed) = params
        taskRepository.setCompleted(task.toTaskEntity(), completed)
    }
}