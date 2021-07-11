package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Pair<Task, Boolean>, Unit>(dispatcher) {
    override suspend fun execute(params: Pair<Task, Boolean>) {
        val (task, completed) = params
        taskRepository.updateTasks(listOf(task.copy(isCompleted = completed)))
    }
}