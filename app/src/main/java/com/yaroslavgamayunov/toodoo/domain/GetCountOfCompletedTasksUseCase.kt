package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.FlowUseCase
import com.yaroslavgamayunov.toodoo.domain.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCountOfCompletedTasksUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : FlowUseCase<Unit, Int>(dispatcher) {
    override fun execute(params: Unit): Flow<Result<Int>> {
        return taskRepository.getCountOfCompletedTasks().map { Result.Success(it) }
    }
}