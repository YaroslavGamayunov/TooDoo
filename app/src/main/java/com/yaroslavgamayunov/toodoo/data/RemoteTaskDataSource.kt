package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.api.TaskApiService
import com.yaroslavgamayunov.toodoo.data.api.TaskSynchronizationRequest
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskApiEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import javax.inject.Inject

class RemoteTaskDataSource @Inject constructor(
    private val webService: TaskApiService,
    @IoDispatcher
    private val coroutineDispatcher: CoroutineDispatcher
) : TaskDataSource {
    override fun getAll(): Flow<List<Task>> {
        return flow {
            emit(webService.getAllTasks().map { it.toTask() })
        }.flowOn(coroutineDispatcher)
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

    override suspend fun addAll(tasks: List<Task>, timeOfAdd: Instant) {
        webService.synchronizeAllChanges(TaskSynchronizationRequest(other = tasks.map {
            it.toTaskApiEntity(
                timeOfAdd,
                timeOfAdd
            )
        }))
    }

    override suspend fun updateAll(tasks: List<Task>, timeOfUpdate: Instant) {
        webService.synchronizeAllChanges(
            TaskSynchronizationRequest(
                other = tasks.map { it.toTaskApiEntity(Instant.ofEpochSecond(0), timeOfUpdate) }
            )
        )
    }

    override suspend fun deleteAll(tasks: List<Task>) {
        webService.synchronizeAllChanges(
            TaskSynchronizationRequest(
                deleted = tasks.map { it.taskId }
            )
        )
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