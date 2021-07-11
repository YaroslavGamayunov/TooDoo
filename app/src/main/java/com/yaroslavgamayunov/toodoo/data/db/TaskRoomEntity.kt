package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskScheduleMode
import java.time.Instant

@Entity(tableName = "tasks")
data class TaskRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: String,
    val description: String,
    @ColumnInfo(name = "completed")
    val isCompleted: Boolean,
    val deadline: Instant,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "schedule_mode")
    val scheduleMode: TaskScheduleMode,
    val priority: TaskPriority
)