package com.yaroslavgamayunov.toodoo.data

import androidx.room.withTransaction
import com.yaroslavgamayunov.toodoo.data.db.*
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskRoomEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.catch
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.mapList
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

interface LocalTaskDataSource : TaskDataSource {
    fun getCompleted(): Flow<List<Task>>
    suspend fun get(id: String): Task

    /**
     * Returns all tasks whose deadline
     * is in [[minDeadline];[maxDeadline]) time segment (excluding right border)
     */
    fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>>
}

class DefaultLocalTaskDataSource @Inject constructor(
    private val taskDatabase: TaskDatabase,
) : LocalTaskDataSource {
    private val taskDao: TaskDao = taskDatabase.taskDao()
    private val taskStateDao: TaskStateDao = taskDatabase.taskStateDao()

    override fun observeAll(): Flow<List<Task>> {
        return taskDao.getAll().mapList { it.toTask() }
    }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> {
        return taskStateDao.getAll().map { it.toTaskWithTimestamps() }
    }

    override fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>> {
        return taskDao.getAllInTimeRange(minDeadline = minDeadline, maxDeadline = maxDeadline)
            .mapList { it.toTask() }
    }

    override suspend fun get(id: String): Task {
        return taskDao.getTask(id).toTask()
    }

    override suspend fun addAll(parameters: TaskModificationParameters): Result<Unit> {
        return Result.catch {
            taskDatabase.withTransaction {
                taskDao.insertAll(parameters.tasks.map { it.toTaskRoomEntity() })
                taskStateDao.insertAll(parameters.tasks.map {
                    TaskState(
                        it.toTaskRoomEntity(),
                        parameters.time,
                        parameters.time
                    )
                })
            }
        }
    }

    override suspend fun updateAll(parameters: TaskModificationParameters): Result<Unit> {
        return Result.catch {
            taskDatabase.withTransaction {
                taskDao.insertAll(parameters.tasks.map { it.toTaskRoomEntity() })
                taskStateDao.updateUpdatedAt(parameters.tasks.map {
                    TaskStateUpdate.UpdatedAt(
                        it.toTaskRoomEntity(),
                        parameters.time
                    )
                })
            }
        }
    }

    override suspend fun deleteAll(parameters: TaskModificationParameters): Result<Unit> {
        return Result.catch {
            taskDatabase.withTransaction {
                taskDao.deleteAll(parameters.tasks.map { it.toTaskRoomEntity() })
                taskStateDao.updateDeletedAt(parameters.tasks.map {
                    TaskStateUpdate.DeletedAt(
                        it.taskId,
                        parameters.time
                    )
                })
            }
        }
    }

    override fun getCompleted(): Flow<List<Task>> {
        return taskDao.getAllCompleted().mapList { it.toTask() }
    }

    override suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps>,
        updated: List<TaskWithTimestamps>,
        deleted: List<Task>,
    ) {
        // Synchronizing deleted tasks
        deleteAll(TaskModificationParameters(deleted))

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