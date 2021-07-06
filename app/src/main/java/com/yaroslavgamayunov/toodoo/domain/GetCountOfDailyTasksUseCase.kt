package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCountOfDailyTasksUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Unit, Int>(dispatcher) {
    override suspend fun execute(params: Unit): Int {
        return taskRepository.getCountOfDailyTasks().first()
    }
}