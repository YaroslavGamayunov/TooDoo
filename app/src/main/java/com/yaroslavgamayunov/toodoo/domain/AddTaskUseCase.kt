package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.data.mappers.TaskEntityMapper
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import kotlinx.coroutines.CoroutineDispatcher
import java.time.Instant
import javax.inject.Inject

data class TaskParameters(
    val description: String,
    val isCompleted: Boolean,
    val deadline: Instant,
    val isScheduledAtExactTime: Boolean,
    val priority: TaskPriority
)

class AddTaskUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) :
    UseCase<TaskParameters, Unit>(dispatcher) {
    override suspend fun execute(params: TaskParameters) {
        taskRepository.insertTask(TaskEntityMapper.toTaskEntity(params))
    }
}