package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.datasync.DefaultTaskSyncStrategy
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncAction
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.TimeUtils
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant

class DefaultTaskSyncStrategyTest {
    private fun createTestTask() = Task(
        "id", "",
        false,
        deadline = TimeUtils.maxZonedDateTime,
        TaskPriority.None
    )

    @Test
    fun `Locally deletes remotely deleted task (remote task is null)`() {
        val task = createTestTask()
        val local = TaskWithTimestamps(
            data = task,
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = null
        )
        val target: TaskWithTimestamps? = null
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.DELETE
    }

    @Test
    fun `Locally deletes remotely deleted task (remote task is not null)`() {
        val local = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = null
        )
        val target: TaskWithTimestamps =
            local.copy(deletedAt = Instant.ofEpochSecond(15))
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.DELETE
    }

    @Test
    fun `Locally adds remotely added task`() {
        val local: TaskWithTimestamps? = null
        val target = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(15),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = null
        )
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.ADD
    }

    @Test
    fun `Locally updates remotely updated task`() {
        val local = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = null
        )
        val target = local.copy(updatedAt = Instant.ofEpochSecond(15))
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.UPDATE
    }


    @Test
    fun `Does nothing when local is updated later than target`() {
        val local = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(20),
            deletedAt = null
        )
        val target = local.copy(updatedAt = Instant.ofEpochSecond(15))
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.NOTHING
    }

    @Test
    fun `Does nothing when local is up-to-date with target`() {
        val task = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(20),
            deletedAt = null
        )
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(task, task, lastSynchronizationTime)

        action shouldBe TaskSyncAction.NOTHING
    }

    @Test
    fun `Does nothing when local and target were both deleted after sync`() {
        val local = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = Instant.ofEpochSecond(20)
        )
        val target = local.copy(deletedAt = Instant.ofEpochSecond(25))
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.NOTHING
    }

    @Test
    fun `Restores locally deleted task if it was updated remotely later than deleted locally`() {
        val local = TaskWithTimestamps(
            data = createTestTask(),
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0),
            deletedAt = Instant.ofEpochSecond(20)
        )
        val target = local.copy(updatedAt = Instant.ofEpochSecond(25), deletedAt = null)
        val lastSynchronizationTime = Instant.ofEpochSecond(10)

        val action = DefaultTaskSyncStrategy(local, target, lastSynchronizationTime)

        action shouldBe TaskSyncAction.UPDATE
    }
}