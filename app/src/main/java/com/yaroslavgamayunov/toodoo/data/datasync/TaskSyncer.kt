package com.yaroslavgamayunov.toodoo.data.datasync

import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import java.time.Instant
import javax.inject.Inject

data class TaskSyncResult(
    val added: List<TaskWithTimestamps> = listOf(),
    val deleted: List<Task> = listOf(),
    val updated: List<TaskWithTimestamps> = listOf()
)

class TaskSyncer @Inject constructor(
    private val taskSyncStrategy: TaskSyncStrategy
) {
    fun sync(
        localData: List<TaskWithTimestamps>,
        remoteData: List<TaskWithTimestamps>,
        previousSynchronizationTime: Instant
    ): TaskSyncResult {
        val deletedTasks = mutableListOf<Task>()
        val addedTasks = mutableListOf<TaskWithTimestamps>()
        val updatedTasks = mutableListOf<TaskWithTimestamps>()

        val localTasksById = localData.map { it.data.taskId to it }.toMap()
        val remoteTasksById = remoteData.map { it.data.taskId to it }.toMap()

        val taskIds = localTasksById.keys + remoteTasksById.keys

        for (id in taskIds) {
            val localTask = localTasksById[id]
            val remoteTask = remoteTasksById[id]

            val action = taskSyncStrategy.invoke(localTask, remoteTask, previousSynchronizationTime)

            when (action) {
                TaskSyncAction.ADD -> addedTasks.add(remoteTask!!)
                TaskSyncAction.UPDATE -> updatedTasks.add(remoteTask!!)
                TaskSyncAction.DELETE -> deletedTasks.add(localTask!!.data)
                else -> Unit
            }
        }

        return TaskSyncResult(added = addedTasks, deleted = deletedTasks, updated = updatedTasks)
    }
}