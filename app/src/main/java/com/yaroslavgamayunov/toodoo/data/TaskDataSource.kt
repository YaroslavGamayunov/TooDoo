package com.yaroslavgamayunov.toodoo.data

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
    suspend fun deleteAll(tasks: List<Task>)

    suspend fun synchronizeChanges(
        addedOrUpdated: List<TaskWithTimestamps>,
        deleted: List<Task>
    )
}

suspend fun TaskDataSource.synchronizeWith(
    target: TaskDataSource,
    previousSynchronizationTime: Instant
) {
    val currentTasks = getAllWithTimestamps().map { it.data.taskId to it }.toMap()
    val targetTasks = target.getAllWithTimestamps().map { it.data.taskId to it }.toMap()

    val deletedTasks = mutableListOf<Task>()
    val addedOrUpdated = mutableListOf<TaskWithTimestamps>()

    for ((taskId, task) in currentTasks) {
        if (!targetTasks.containsKey(taskId) && task.updatedAt < previousSynchronizationTime) {
            deletedTasks.add(task.data)
        }
    }

    for ((targetTaskId, targetTask) in targetTasks.entries) {
        if (currentTasks.containsKey(targetTaskId)) {
            val currentTask = currentTasks[targetTaskId]!!
            if (targetTask.updatedAt.isAfter(currentTask.updatedAt)) {
                addedOrUpdated.add(targetTask)
            }
        } else {
            addedOrUpdated.add(targetTask)
        }
    }

    synchronizeChanges(addedOrUpdated = addedOrUpdated, deleted = deletedTasks)
}