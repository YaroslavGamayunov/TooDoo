package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.TaskEntityMapper
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class AddTaskUseCase @Inject constructor(
    @Named("ioDispatcher")
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) :
    UseCase<Task, Unit>(dispatcher) {
    override suspend fun execute(params: Task) {
        taskRepository.insertTask(TaskEntityMapper.toTaskEntity(params))
    }
}