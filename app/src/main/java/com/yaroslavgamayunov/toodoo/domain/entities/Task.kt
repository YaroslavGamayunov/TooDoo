package com.yaroslavgamayunov.toodoo.domain.entities

import java.time.ZonedDateTime

enum class TaskPriority(val level: Int) {
    None(0),
    Low(1),
    High(2)
}

enum class TaskScheduleMode(val id: Int) {
    ExactTime(0),
    NotExactTime(1),
    Unspecified(2)
}

data class Task(
    val taskId: Int,
    val description: String,
    val isCompleted: Boolean,
    val deadline: ZonedDateTime,
    val scheduleMode: TaskScheduleMode,
    val priority: TaskPriority
)
