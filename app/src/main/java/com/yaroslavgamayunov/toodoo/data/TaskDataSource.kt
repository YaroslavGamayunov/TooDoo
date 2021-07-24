package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncResult
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskDataSource {
    fun observeAll(): Flow<List<Task>>
    suspend fun getAllWithTimestamps(): List<TaskWithTimestamps>

    suspend fun addAll(parameters: TaskModificationParameters): Result<Unit>
    suspend fun updateAll(parameters: TaskModificationParameters): Result<Unit>
    suspend fun deleteAll(parameters: TaskModificationParameters): Result<Unit>

    suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps> = listOf(),
        updated: List<TaskWithTimestamps> = listOf(),
        deleted: List<Task> = listOf(),
    )

    suspend fun synchronizeChanges(
        syncResult: TaskSyncResult
    ) = synchronizeChanges(
        added = syncResult.added,
        updated = syncResult.updated,
        deleted = syncResult.deleted
    )
}

/**
 * Data class holding information for one of typical [TaskDataSource]'s operations:
 * [TaskDataSource.addAll], [TaskDataSource.updateAll], [TaskDataSource.deleteAll]
 *
 * Holds [tasks] to perform operation on and [time] of operation
 */
data class TaskModificationParameters(
    val tasks: List<Task>,
    val time: Instant = Instant.now()
)