package com.yaroslavgamayunov.toodoo

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.domain.common.FlowUseCase
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCountOfCompletedTasksUseCase(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(params: Unit): Flow<Result<Int>> {
        return taskRepository.getNumberOfCompletedTasks().map { Result.Success(it) }
    }
}