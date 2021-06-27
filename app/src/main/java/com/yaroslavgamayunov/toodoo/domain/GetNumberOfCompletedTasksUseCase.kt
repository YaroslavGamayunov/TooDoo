package com.yaroslavgamayunov.toodoo

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetNumberOfCompletedTasksUseCase(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Unit, Flow<Int>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<Int> {
        return taskRepository.getNumberOfCompletedTasks()
    }
}