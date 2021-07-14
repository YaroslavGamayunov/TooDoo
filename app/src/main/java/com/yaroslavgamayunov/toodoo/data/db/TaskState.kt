package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import java.time.Instant

@Entity(tableName = "task_states", primaryKeys = ["task_id"])
data class TaskState(
    @Embedded
    val task: TaskRoomEntity,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null,
)

fun TaskState.toTaskWithTimestamps() =
    TaskWithTimestamps(task.toTask(), createdAt, updatedAt, deletedAt)

sealed class TaskStateUpdate {
    data class UpdatedAt(
        @Embedded
        val task: TaskRoomEntity,
        @ColumnInfo(name = "updated_at") val updatedAt: Instant
    ) : TaskStateUpdate()

    data class CreatedAt(
        @Embedded
        val task: TaskRoomEntity,
        @ColumnInfo(name = "created_at") val createdAt: Instant
    ) : TaskStateUpdate()

    data class DeletedAt(
        @ColumnInfo(name = "task_id") val taskId: String,
        @ColumnInfo(name = "deleted_at") val deletedAt: Instant
    ) : TaskStateUpdate()
}