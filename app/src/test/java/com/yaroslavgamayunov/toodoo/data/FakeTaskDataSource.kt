package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

/**
 * TaskDataSource independent from network, database and android-related things
 */
class FakeTaskDataSource(initialData: List<TaskWithTimestamps>) : TaskDataSource,
    LocalTaskDataSource {
    private val tasks = MutableStateFlow(initialData.map { it.data.taskId to it }.toMap())

    override fun getCompleted(): Flow<List<Task>> {
        return tasks.map {
            it.values
                .map { taskWithTimestamps -> taskWithTimestamps.data }
                .filter { task -> task.isCompleted }
        }
    }

    override suspend fun get(id: String): Task {
        return tasks.value[id]!!.data
    }

    override fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>> {
        return tasks.map {
            it.values
                .map { taskWithTimestamps -> taskWithTimestamps.data }
                .filter { task ->
                    val deadline = task.deadline.toInstant()
                    deadline >= minDeadline && deadline < maxDeadline
                }
        }
    }

    override fun observeAll(): Flow<List<Task>> =
        tasks.map {
            it.values
                .filter { task -> task.deletedAt == null }
                .map { taskWithTimestamps -> taskWithTimestamps.data }
        }

    override suspend fun getAllWithTimestamps(): List<TaskWithTimestamps> =
        tasks.value.values.toList()

    override suspend fun addAll(parameters: TaskModificationParameters): Result<Unit> {
        tasks.value += parameters.tasks.map {
            it.taskId to
                    TaskWithTimestamps(data = it,
                        createdAt = parameters.time,
                        updatedAt = parameters.time)
        }
        return Result.Success(Unit)
    }

    override suspend fun updateAll(parameters: TaskModificationParameters): Result<Unit> {
        tasks.value += parameters.tasks.map {
            it.taskId to tasks.value[it.taskId]!!.copy(
                data = it,
                updatedAt = parameters.time
            )
        }

        return Result.Success(Unit)
    }

    override suspend fun deleteAll(parameters: TaskModificationParameters): Result<Unit> {
        val modifiedTasks =
            parameters.tasks.map { tasks.value[it.taskId]!!.copy(deletedAt = parameters.time) }

        tasks.value += modifiedTasks.map { it.data.taskId to it }
        return Result.Success(Unit)
    }

    override suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps>,
        updated: List<TaskWithTimestamps>,
        deleted: List<Task>,
    ) {
        tasks.value += added.map { it.data.taskId to it }
        tasks.value += updated.map { it.data.taskId to it }

        // TODO: Make time remain unchanged across tests (to avoid flaky tests)
        val time = Instant.now()
        deleteAll(TaskModificationParameters(deleted, time))
    }
}