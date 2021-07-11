package com.yaroslavgamayunov.toodoo.domain.entities

import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskScheduleMode
import java.time.ZonedDateTime

data class Task(
    val taskId: String,
    val description: String,
    val isCompleted: Boolean,
    val deadline: ZonedDateTime,
    val scheduleMode: TaskScheduleMode,
    val priority: TaskPriority
)
