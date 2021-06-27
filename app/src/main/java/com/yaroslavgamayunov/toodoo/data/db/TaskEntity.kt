package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import java.time.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Int,
    val description: String,
    @ColumnInfo(name = "completed")
    val isCompleted: Boolean,
    val deadline: Instant,
    @ColumnInfo(name = "schedule_mode")
    val scheduleMode: TaskScheduleMode,
    val priority: TaskPriority
)