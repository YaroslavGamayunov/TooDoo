package com.yaroslavgamayunov.toodoo.data.mappers

import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import com.yaroslavgamayunov.toodoo.domain.TaskParameters
import com.yaroslavgamayunov.toodoo.domain.entities.Task

object TaskEntityMapper {
    fun toTaskEntity(task: Task) = TaskEntity(
        taskId = task.taskId,
        description = task.description,
        isCompleted = task.isCompleted,
        deadline = task.deadline,
        isScheduledAtExactTime = task.isScheduledAtExactTime,
        priority = task.priority
    )

    fun toTask(taskEntity: TaskEntity) = Task(
        taskId = taskEntity.taskId,
        description = taskEntity.description,
        isCompleted = taskEntity.isCompleted,
        deadline = taskEntity.deadline,
        isScheduledAtExactTime = taskEntity.isScheduledAtExactTime,
        priority = taskEntity.priority
    )

    fun toTaskEntity(parameters: TaskParameters) = TaskEntity(
        taskId = 0,
        description = parameters.description,
        isCompleted = parameters.isCompleted,
        deadline = parameters.deadline,
        isScheduledAtExactTime = parameters.isScheduledAtExactTime,
        priority = parameters.priority
    )
}