package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.db.TaskDao
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskRoomEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class LocalTaskDataSource @Inject constructor(
    taskDatabase: TaskDatabase
) : TaskDataSource {
    private val taskDao: TaskDao = taskDatabase.taskDao()

    override fun getAll(): Flow<List<Task>> {
        return taskDao.getAll().map { tasks -> tasks.map { it.toTask() } }
    }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> {
        val tasks = taskDao.getAll().first()
        return tasks.map {
            TaskWithTimestamps(it.toTask(), it.createdAt, it.updatedAt)
        }
    }

    override suspend fun get(id: String): Task {
        return taskDao.getTask(id).toTask()
    }

    override suspend fun addAll(tasks: List<Task>, timeOfAdd: Instant) {
        taskDao.insertAll(
            tasks.map {
                it.toTaskRoomEntity(
                    createdAt = timeOfAdd,
                    updatedAt = timeOfAdd
                )
            }
        )
    }

    override suspend fun updateAll(tasks: List<Task>, timeOfUpdate: Instant) {
        val timestamps = taskDao.getTimestamps(tasks.map { it.taskId }).groupBy { it.taskId }
            .mapValues { it.value.first() }

        taskDao.insertAll(
            tasks.map {
                it.toTaskRoomEntity(
                    createdAt = timestamps[it.taskId]!!.createdAt,
                    updatedAt = timeOfUpdate
                )
            }
        )
    }

    override suspend fun deleteAll(tasks: List<Task>) {
        taskDao.deleteAll(tasks.map { it.toTaskRoomEntity() })
    }

    override suspend fun synchronizeChanges(
        addedOrUpdated: List<TaskWithTimestamps>,
        deleted: List<Task>
    ) {
        taskDao.insertAll(addedOrUpdated.map {
            it.data.toTaskRoomEntity(
                it.createdAt,
                it.updatedAt
            )
        })
        taskDao.deleteAll(deleted.map { it.toTaskRoomEntity() })
    }
}