package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.api.TaskApiService
import com.yaroslavgamayunov.toodoo.data.api.TaskSynchronizationRequest
import com.yaroslavgamayunov.toodoo.data.mappers.toTask
import com.yaroslavgamayunov.toodoo.data.mappers.toTaskApiEntity
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.catch
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.exception.resolveNetworkFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import javax.inject.Inject


class RemoteTaskDataSource @Inject constructor(
    private val webService: TaskApiService,
    @IoDispatcher
    private val coroutineDispatcher: CoroutineDispatcher,
) : TaskDataSource {
    override fun observeAll(): Flow<List<Task>> {
        return flow {
            val tasks = webService.getAllTasks().map { it.toTask() }
            emit(tasks)
        }.flowOn(coroutineDispatcher)
    }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> {
        return webService.getAllTasks().map {
            TaskWithTimestamps(
                it.toTask(),
                Instant.ofEpochSecond(it.createdAt),
                Instant.ofEpochSecond(it.updatedAt)
            )
        }
    }

    override suspend fun addAll(parameters: TaskModificationParameters): Result<Unit> {
        val tasksForSync =
            parameters.tasks.map {
                it.toTaskApiEntity(
                    createdAt = parameters.time,
                    updatedAt = parameters.time
                )
            }

        return makeNetworkRequest {
            if (tasksForSync.size == 1) {
                val task = tasksForSync.first()
                webService.addTask(task)
            } else {
                webService.synchronizeAllChanges(TaskSynchronizationRequest(other = tasksForSync))
            }
        }
    }

    override suspend fun updateAll(parameters: TaskModificationParameters): Result<Unit> {
        val tasksForSync =
            parameters.tasks.map {
                it.toTaskApiEntity(
                    Instant.ofEpochSecond(0), parameters.time
                )
            }

        return makeNetworkRequest {
            if (tasksForSync.size == 1) {
                val task = tasksForSync.first()
                webService.updateTask(task.taskId, task)
            } else {
                webService.synchronizeAllChanges(TaskSynchronizationRequest(other = tasksForSync))
            }
        }
    }

    override suspend fun deleteAll(parameters: TaskModificationParameters): Result<Unit> {
        return makeNetworkRequest {
            if (parameters.tasks.size == 1) {
                webService.deleteTask(parameters.tasks.first().taskId)
            } else {
                webService.synchronizeAllChanges(
                    TaskSynchronizationRequest(deleted = parameters.tasks.map { it.taskId })
                )
            }
        }
    }

    override suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps>,
        updated: List<TaskWithTimestamps>,
        deleted: List<Task>,
    ) {
        val synchronizationRequest = TaskSynchronizationRequest(
            deleted = deleted.map { it.taskId },
            other = (added + updated).map { it.data.toTaskApiEntity(it.createdAt, it.updatedAt) }
        )

        webService.synchronizeAllChanges(synchronizationRequest)
    }

    private suspend fun <T> makeNetworkRequest(f: suspend () -> T): Result<Unit> {
        return Result.catch(failureResolver = ::resolveNetworkFailure) {
            f()
        }
    }
}