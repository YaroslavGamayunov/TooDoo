package com.yaroslavgamayunov.toodoo.data.datasync

import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.data.model.isDeleted
import java.time.Instant

/**
 * Takes [TaskWithTimestamps] of local task and target task or null
 * if task is not presented in local or target data source respectively
 * Returns [TaskSynchronizationAction] to be done to the local source
 */
typealias TaskSynchronizationStrategy = (
    localTask: TaskWithTimestamps?,
    targetTask: TaskWithTimestamps?,
    lastSynchronizationTime: Instant
) -> TaskSynchronizationAction


enum class TaskSynchronizationAction {
    /**
     * Add target task to local datasource
     */
    ADD,

    /**
     * Update local datasource with target task
     */
    UPDATE,

    /**
     * Delete local task
     */
    DELETE,

    /**
     * Do nothing to local datasource
     */
    NOTHING
}

object DefaultTaskSynchronizationStrategy : TaskSynchronizationStrategy {
    private fun TaskWithTimestamps?.isDeletedAfterSync(
        remote: TaskWithTimestamps,
        lastSynchronizationTime: Instant
    ): Boolean {
        return (this == null && remote.createdAt < lastSynchronizationTime) ||
                (this?.deletedAt?.let { it > lastSynchronizationTime } ?: false)
    }

    override fun invoke(
        local: TaskWithTimestamps?,
        target: TaskWithTimestamps?,
        lastSynchronizationTime: Instant
    ): TaskSynchronizationAction {
        if (local == null && target != null) {
            val isDeletedLocally = local.isDeletedAfterSync(target, lastSynchronizationTime)
            if (!isDeletedLocally) {
                return TaskSynchronizationAction.ADD
            }
        }

        if (local != null && target == null) {
            val isDeletedRemotely = target.isDeletedAfterSync(local, lastSynchronizationTime)
            if (isDeletedRemotely && !local.isDeleted) {
                return TaskSynchronizationAction.DELETE
            }
        }

        if (local != null && target != null) {
            target.deletedAt?.let {
                if (it > lastSynchronizationTime && !local.isDeleted) {
                    return TaskSynchronizationAction.DELETE
                }
            }

            if (target.updatedAt > local.updatedAt) {
                return TaskSynchronizationAction.UPDATE
            }
        }
        return TaskSynchronizationAction.NOTHING
    }
}