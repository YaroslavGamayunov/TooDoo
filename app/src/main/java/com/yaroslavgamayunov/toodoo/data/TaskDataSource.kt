package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.datasync.DefaultTaskSynchronizationStrategy
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSynchronizationAction
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSynchronizationStrategy
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskDataSource {
    fun getAll(): Flow<List<Task>>
    suspend fun getAllWithTimestamps(): List<TaskWithTimestamps>

    suspend fun get(id: String): Task

    suspend fun addAll(tasks: List<Task>, timeOfAdd: Instant = Instant.now())
    suspend fun updateAll(tasks: List<Task>, timeOfUpdate: Instant = Instant.now())
    suspend fun deleteAll(tasks: List<Task>, timeOfDelete: Instant = Instant.now())

    suspend fun synchronizeChanges(
        added: List<TaskWithTimestamps> = listOf(),
        updated: List<TaskWithTimestamps> = listOf(),
        deleted: List<Task> = listOf(),
    )
}

suspend fun TaskDataSource.synchronizeWith(
    target: TaskDataSource,
    previousSynchronizationTime: Instant,
    taskSynchronizationStrategy: TaskSynchronizationStrategy = DefaultTaskSynchronizationStrategy
) {
    val localTasks = getAllWithTimestamps().map { it.data.taskId to it }.toMap()
    val targetTasks = target.getAllWithTimestamps().map { it.data.taskId to it }.toMap()

    val idsToSynchronize = (localTasks.keys + targetTasks.keys).toSet()

    val diffList = idsToSynchronize.map { taskId -> localTasks[taskId] to targetTasks[taskId] }

    val deletedTasks = mutableListOf<Task>()
    val addedTasks = mutableListOf<TaskWithTimestamps>()
    val updatedTasks = mutableListOf<TaskWithTimestamps>()

    for ((localTask, targetTask) in diffList) {
        val action =
            taskSynchronizationStrategy.invoke(localTask, targetTask, previousSynchronizationTime)
        when (action) {
            TaskSynchronizationAction.ADD -> addedTasks.add(targetTask!!)
            TaskSynchronizationAction.UPDATE -> updatedTasks.add(targetTask!!)
            TaskSynchronizationAction.DELETE -> deletedTasks.add(localTask!!.data)
            else -> Unit
        }
    }

    synchronizeChanges(added = addedTasks, deleted = deletedTasks, updated = updatedTasks)
}

