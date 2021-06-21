package com.yaroslavgamayunov.toodoo.domain.entities

import androidx.room.ColumnInfo
import java.time.Instant

enum class TaskPriority(val level: Int) {
    None(0),
    Low(1),
    High(2)
}

data class Task(
    val taskId: Int,
    val description: String,
    val isCompleted: Boolean,
    val deadline: Instant,
    val isScheduledAtExactTime: Boolean,
    val priority: TaskPriority
)
