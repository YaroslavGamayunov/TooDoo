package com.yaroslavgamayunov.toodoo.data.mappers

import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import com.yaroslavgamayunov.toodoo.domain.entities.Task

fun Task.toTaskEntity() = TaskEntity(
    taskId = taskId,
    description = description,
    isCompleted = isCompleted,
    deadline = deadline,
    scheduleMode = scheduleMode,
    priority = priority
)

fun TaskEntity.toTask() = Task(
    taskId = taskId,
    description = description,
    isCompleted = isCompleted,
    deadline = deadline,
    scheduleMode = scheduleMode,
    priority = priority
)