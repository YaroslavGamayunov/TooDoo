package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.SampleData
import com.yaroslavgamayunov.toodoo.data.datasync.DefaultTaskSyncStrategy
import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncer
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class DefaultTaskRepositoryTest {

    @ExperimentalCoroutinesApi
    @Test
    fun `Adds task correctly`() {
        val localTaskDataSource = FakeTaskDataSource(listOf())
        val remoteTaskDataSource = FakeTaskDataSource(listOf())

        val repository =
            DefaultTaskRepository(
                localTaskDataSource,
                remoteTaskDataSource,
                TaskSyncer(DefaultTaskSyncStrategy),
                FakePreferenceHelper(),
            )

        runBlockingTest {
            repository.addTasks(SampleData.tasks)
            val tasks = repository.getAllTasks().first()

            tasks should beOfType<Result.Success<List<Task>>>()
            val taskData = (tasks as Result.Success).data
            taskData.sortedBy { it.taskId } shouldBe SampleData.tasks.sortedBy { it.taskId }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Deletes tasks correctly`() {
        val localTaskDataSource = FakeTaskDataSource(SampleData.tasksWithTimestamps)
        val remoteTaskDataSource = FakeTaskDataSource(SampleData.tasksWithTimestamps)

        val repository =
            DefaultTaskRepository(
                localTaskDataSource,
                remoteTaskDataSource,
                TaskSyncer(DefaultTaskSyncStrategy),
                FakePreferenceHelper(),
            )

        runBlockingTest {
            repository.deleteTasks(SampleData.tasksWithTimestamps.takeLast(2).map { it.data })
            val tasks = repository.getAllTasks().first()

            tasks should beOfType<Result.Success<List<Task>>>()
            val taskData = (tasks as Result.Success).data

            taskData.sortedBy { it.taskId } shouldBe
                    SampleData.tasks.dropLast(2).sortedBy { it.taskId }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Updates tasks correctly`() {
        val localTaskDataSource = FakeTaskDataSource(SampleData.tasksWithTimestamps)
        val remoteTaskDataSource = FakeTaskDataSource(SampleData.tasksWithTimestamps)

        val repository =
            DefaultTaskRepository(
                localTaskDataSource,
                remoteTaskDataSource,
                TaskSyncer(DefaultTaskSyncStrategy),
                FakePreferenceHelper(),
            )

        runBlockingTest {
            val task = SampleData.tasksWithTimestamps[3]
            val updatedTask = task.data.copy(description = "changed")

            repository.updateTasks(listOf(updatedTask))

            val tasks = repository.getAllTasks().first()
            tasks should beOfType<Result.Success<List<Task>>>()

            val taskData = (tasks as Result.Success).data

            val updatedData = SampleData.tasks.toMutableList().also { it[3] = updatedTask }

            taskData.sortedBy { it.taskId } shouldBe
                    updatedData.sortedBy { it.taskId }
        }
    }
}