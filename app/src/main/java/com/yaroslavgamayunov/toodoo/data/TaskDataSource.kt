package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskDataSource {
    fun getAll(): Flow<List<Task>>
    suspend fun getAllWithTimestamps(): List<TaskWithTimestamps>

    suspend fun get(id: String): Task
    suspend fun add(task: Task, timeOfAdd: Instant = Instant.now())
    suspend fun update(task: Task, timeOfUpdate: Instant = Instant.now())
    suspend fun delete(task: Task)

    suspend fun synchronizeChanges(
        addedOrUpdated: List<TaskWithTimestamps>,
        deleted: List<Task>
    )
}

suspend fun TaskDataSource.synchronizeWith(
    target: TaskDataSource
) {
    val currentTasks = getAllWithTimestamps().map { it.data.taskId to it }.toMap()
    val targetTasks = target.getAllWithTimestamps().map { it.data.taskId to it }.toMap()

    val deletedTasks = mutableListOf<Task>()
    val addedOrUpdated = mutableListOf<TaskWithTimestamps>()

    for ((taskId, task) in currentTasks) {
        if (!targetTasks.containsKey(taskId)) {
            deletedTasks.add(task.data)
        }
    }

    for ((taskId, targetTask) in targetTasks.entries) {
        if (currentTasks.containsKey(taskId)) {
            val currentTask = currentTasks[taskId]!!
            if (targetTask.updatedAt.isAfter(currentTask.updatedAt)) {
                addedOrUpdated.add(targetTask)
            }
        }
    }

    synchronizeChanges(addedOrUpdated = addedOrUpdated, deleted = deletedTasks)
}