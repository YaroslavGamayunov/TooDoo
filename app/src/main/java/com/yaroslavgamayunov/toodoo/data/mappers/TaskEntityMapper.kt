package com.yaroslavgamayunov.toodoo.data.mappers

import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import com.yaroslavgamayunov.toodoo.domain.entities.Task

object TaskEntityMapper {
    fun toTaskEntity(task: Task) = TaskEntity(
        taskId = task.taskId,
        description = task.description,
        isCompleted = task.isCompleted,
        deadline = task.deadline,
        scheduleMode = task.scheduleMode,
        priority = task.priority
    )

    fun toTask(taskEntity: TaskEntity) = Task(
        taskId = taskEntity.taskId,
        description = taskEntity.description,
        isCompleted = taskEntity.isCompleted,
        deadline = taskEntity.deadline,
        scheduleMode = taskEntity.scheduleMode,
        priority = taskEntity.priority
    )
}