package com.yaroslavgamayunov.toodoo.data.mappers

import com.yaroslavgamayunov.toodoo.data.api.TaskApiEntity
import com.yaroslavgamayunov.toodoo.data.db.TaskRoomEntity
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.util.taskScheduleMode
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Task.toTaskRoomEntity(
    createdAt: Instant = Instant.MIN,
    updatedAt: Instant = Instant.MIN
) =
    TaskRoomEntity(
        taskId = taskId,
        description = description,
        isCompleted = isCompleted,
        deadline = deadline.toInstant(),
        priority = priority,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun TaskRoomEntity.toTask() = Task(
    taskId = taskId,
    description = description,
    isCompleted = isCompleted,
    deadline = deadline.atZone(ZoneId.systemDefault()),
    scheduleMode = deadline.taskScheduleMode(),
    priority = priority
)

fun Task.toTaskApiEntity(createdAt: Instant, updatedAt: Instant) = TaskApiEntity(
    taskId = taskId,
    description = description,
    isCompleted = isCompleted,
    deadline = deadline.toInstant().epochSecond,
    priority = priority,
    createdAt = createdAt.epochSecond,
    updatedAt = updatedAt.epochSecond
)

fun TaskApiEntity.toTask() = Task(
    taskId = taskId,
    description = description,
    isCompleted = isCompleted,
    deadline = ZonedDateTime.ofInstant(Instant.ofEpochSecond(deadline), ZoneId.systemDefault()),
    scheduleMode = Instant.ofEpochSecond(deadline).taskScheduleMode(),
    priority = priority
)
