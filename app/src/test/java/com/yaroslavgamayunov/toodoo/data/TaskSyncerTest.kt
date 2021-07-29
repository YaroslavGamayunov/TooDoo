package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.datasync.DefaultTaskSyncStrategy
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncResult
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncer
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TaskSyncerTest {
    @Test
    fun `Local data is the same as remote data after updates applied`() {
        val deadlineTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(100), ZoneOffset.UTC)

        val localData = listOf(
            TaskWithTimestamps(
                data = Task(taskId = "1", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(0),
                updatedAt = Instant.ofEpochSecond(0)
            ),
            TaskWithTimestamps(
                data = Task(taskId = "2", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(0),
                updatedAt = Instant.ofEpochSecond(0),
                deletedAt = Instant.ofEpochSecond(20)
            ),
            TaskWithTimestamps(
                data = Task(taskId = "3", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(20),
                updatedAt = Instant.ofEpochSecond(20)
            )
        )

        val remoteData = listOf(
            TaskWithTimestamps(
                data = Task(taskId = "1", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(0),
                updatedAt = Instant.ofEpochSecond(0),
                deletedAt = Instant.ofEpochSecond(20)
            ),
            TaskWithTimestamps(
                data = Task(taskId = "2", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(0),
                updatedAt = Instant.ofEpochSecond(0),
            ),
            TaskWithTimestamps(
                data = Task(taskId = "4", "", false, deadlineTime, priority = TaskPriority.None),
                createdAt = Instant.ofEpochSecond(20),
                updatedAt = Instant.ofEpochSecond(20)
            )
        )

        val latestSyncTime = Instant.ofEpochSecond(0)

        val syncer = TaskSyncer(DefaultTaskSyncStrategy)
        val updatesToLocal = syncer.sync(localData, remoteData, latestSyncTime)
        val updatesToRemote = syncer.sync(remoteData, localData, latestSyncTime)


        val timeOfSync = Instant.ofEpochMilli(30)
        val local =
            applyChanges(localData, updatesToLocal, timeOfSync).filter { it.deletedAt == null }
        val remote =
            applyChanges(remoteData, updatesToRemote, timeOfSync).filter { it.deletedAt == null }

        local shouldBe remote
    }

    private fun applyChanges(
        data: List<TaskWithTimestamps>,
        changes: TaskSyncResult,
        timeOfSync: Instant,
    ): List<TaskWithTimestamps> {
        val taskById = data.map { it.data.taskId to it }.toMap().toMutableMap()
        taskById.putAll(changes.updated.map { it.data.taskId to it })

        changes.deleted.forEach {
            taskById[it.taskId] = taskById[it.taskId]!!.copy(deletedAt = timeOfSync)
        }

        return (taskById.values + changes.added).sortedBy { it.data.taskId }
    }
}