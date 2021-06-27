package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.TaskEntityMapper
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetSingleTaskByIdUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<Int, Task>(dispatcher) {
    override suspend fun execute(params: Int): Task {
        return TaskEntityMapper.toTask(taskRepository.getTask(id = params))
    }
}