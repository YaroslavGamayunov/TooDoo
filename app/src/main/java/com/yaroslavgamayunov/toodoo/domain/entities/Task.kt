package com.yaroslavgamayunov.toodoo.domain.entities

import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import java.time.ZonedDateTime

data class Task(
    val taskId: String,
    val description: String,
    val isCompleted: Boolean,
    val deadline: ZonedDateTime,
    val scheduleMode: TaskScheduleMode,
    val priority: TaskPriority
)

enum class TaskScheduleMode {
    ByTime,
    ByDate,
    Unspecified
}
