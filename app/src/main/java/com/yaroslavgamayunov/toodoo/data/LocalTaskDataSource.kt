package com.yaroslavgamayunov.toodoo.data

import androidx.room.withTransaction
import com.yaroslavgamayunov.toodoo.data.db.*
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskRoomEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.mapList
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class LocalTaskDataSource @Inject constructor(
    private val taskDatabase: TaskDatabase
) : TaskDataSource {
    private val taskDao: TaskDao = taskDatabase.taskDao()
    private val taskStateDao: TaskStateDao = taskDatabase.taskStateDao()

    override fun getAll(): Flow<List<Task>> {
        return taskDao.getAll()
            .mapList { it.toTask() }
    }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> {
        return taskStateDao.getAll()
            .map { it.toTaskWithTimestamps() }
    }

    override fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>> {
        return taskDao.getAllInTimeRange(minDeadline = minDeadline, maxDeadline = maxDeadline)
            .mapList { it.toTask() }
    }

    override suspend fun get(id: String): Task {
        return taskDao.getTask(id).toTask()
    }

    override suspend fun addAll(tasks: List<Task>, timeOfAdd: Instant) {
        taskDatabase.withTransaction {
            taskDao.insertAll(tasks.map { it.toTaskRoomEntity() })
            taskStateDao.insertAll(tasks.map {
                TaskState(
                    it.toTaskRoomEntity(),
                    timeOfAdd,
                    timeOfAdd
                )
            })
        }
    }

    override suspend fun updateAll(tasks: List<Task>, timeOfUpdate: Instant) {
        taskDatabase.withTransaction {
            taskDao.insertAll(tasks.map { it.toTaskRoomEntity() })
            taskStateDao.updateUpdatedAt(tasks.map {
                TaskStateUpdate.UpdatedAt(
                    it.toTaskRoomEntity(),
                    timeOfUpdate
                )
            })
        }
    }

    override suspend fun deleteAll(tasks: List<Task>, timeOfDelete: Instant) {
        taskDatabase.withTransaction {
            taskDao.deleteAll(tasks.map { it.toTaskRoomEntity() })
            taskStateDao.updateDeletedAt(tasks.map {
                TaskStateUpdate.DeletedAt(
                    it.taskId,
                    timeOfDelete
                )
            })
        }
    }

    override suspend fun getCompleted(): Flow<List<Task>> {
        return taskDao.getAllCompleted().mapList { it.toTask() }
    }

    override suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps>,
        updated: List<TaskWithTimestamps>,
        deleted: List<Task>,
    ) {
        // Synchronizing deleted tasks
        deleteAll(deleted, Instant.now())

        // Synchronizing updated tasks
        taskDatabase.withTransaction {
            taskDao.insertAll(updated.map { it.data.toTaskRoomEntity() })
            taskStateDao.updateUpdatedAt(updated.map {
                TaskStateUpdate.UpdatedAt(
                    it.data.toTaskRoomEntity(),
                    it.updatedAt
                )
            })
        }

        // Synchronizing added tasks
        taskDatabase.withTransaction {
            taskDao.insertAll(added.map { it.data.toTaskRoomEntity() })
            taskStateDao.insertAll(added.map {
                TaskState(
                    task = it.data.toTaskRoomEntity(),
                    updatedAt = it.updatedAt,
                    createdAt = it.createdAt,
                    deletedAt = it.deletedAt
                )
            })
        }
    }
}