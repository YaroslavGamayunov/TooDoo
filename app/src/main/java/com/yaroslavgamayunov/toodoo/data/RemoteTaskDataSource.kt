package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.api.TaskApiService
import com.yaroslavgamayunov.toodoo.data.api.TaskSynchronizationRequest
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskApiEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject

class RemoteTaskDataSource @Inject constructor(
    private val webService: TaskApiService,
) : TaskDataSource {
    override fun getAll(): Flow<List<Task>> {
        return flow {
            emit(webService.getAllTasks().map { it.toTask() })
        }
    }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> {
        return webService.getAllTasks()
            .map {
                TaskWithTimestamps(
                    it.toTask(),
                    Instant.ofEpochSecond(it.createdAt),
                    Instant.ofEpochSecond(it.updatedAt)
                )
            }
    }

    // Since api doesn't allow to get task by id I had to implement this weird slow function
    override suspend fun get(id: String): Task {
        return getAll().first().find { it.taskId == id }
            ?: throw Exception("Task with id=${id} was not found on the server")
    }

    override suspend fun add(task: Task, timeOfAdd: Instant) {
        webService.addTask(task.toTaskApiEntity(timeOfAdd, timeOfAdd))
    }

    override suspend fun update(task: Task, timeOfUpdate: Instant) {
        webService.updateTask(
            task.taskId,
            task.toTaskApiEntity(Instant.ofEpochSecond(0), timeOfUpdate)
        )
    }

    override suspend fun delete(task: Task) {
        webService.deleteTask(task.taskId)
    }

    override suspend fun synchronizeChanges(
        addedOrUpdated: List<TaskWithTimestamps>,
        deleted: List<Task>
    ) {
        webService.synchronizeAllChanges(
            TaskSynchronizationRequest(
                deleted = deleted.map { it.taskId },
                other = addedOrUpdated.map { it.data.toTaskApiEntity(it.createdAt, it.updatedAt) }
            )
        )
    }
}