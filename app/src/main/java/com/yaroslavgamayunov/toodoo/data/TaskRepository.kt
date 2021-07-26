package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.datasync.TaskSyncer
import com.yaroslavgamayunov.toodoo.di.LocalDataSource
import com.yaroslavgamayunov.toodoo.di.RemoteDataSource
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.catch
import com.yaroslavgamayunov.toodoo.domain.common.teePipeTo
import com.yaroslavgamayunov.toodoo.domain.common.then
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.exception.Failure
import com.yaroslavgamayunov.toodoo.util.PreferenceDelegate
import com.yaroslavgamayunov.toodoo.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

interface TaskRepository {
    suspend fun refreshData(): Result<Unit>

    fun getAllTasks(): Flow<Result<List<Task>>>
    fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<Result<List<Task>>>
    suspend fun getTask(id: String): Result<Task>

    fun getCompletedTasks(): Flow<Result<List<Task>>>

    suspend fun updateTasks(tasks: List<Task>): Result<Unit>
    suspend fun addTasks(tasks: List<Task>): Result<Unit>
    suspend fun deleteTasks(tasks: List<Task>): Result<Unit>
}

class DefaultTaskRepository @Inject constructor(
    @LocalDataSource
    private val localTaskDataSource: LocalTaskDataSource,
    @RemoteDataSource
    private val remoteTaskDataSource: TaskDataSource,
    private val taskSyncer: TaskSyncer,
    preferenceHelper: PreferenceHelper,
) : TaskRepository {

    private var lastSynchronizationTime: Long
            by PreferenceDelegate(preferenceHelper, SYNC_TIME_PREFERENCE_KEY, 0)

    override suspend fun refreshData(): Result<Unit> {
        return Result.catch(failureResolver = { Failure.SynchronizationError }) {
            val lastSync = Instant.ofEpochSecond(lastSynchronizationTime)
            lastSynchronizationTime = Instant.now().epochSecond

            val localData = localTaskDataSource.getAllWithTimestamps()
            val remoteData = remoteTaskDataSource.getAllWithTimestamps()

            localTaskDataSource
                .synchronizeChanges(taskSyncer.sync(localData, remoteData, lastSync))

            remoteTaskDataSource
                .synchronizeChanges(taskSyncer.sync(remoteData, localData, lastSync))
        }
    }

    override fun getAllTasks(): Flow<Result<List<Task>>> {
        return localTaskDataSource.observeAll().toResultFlow()
    }

    override fun getAllInTimeRange(
        minDeadline: Instant,
        maxDeadline: Instant,
    ): Flow<Result<List<Task>>> {
        return localTaskDataSource.getAllInTimeRange(minDeadline, maxDeadline).toResultFlow()
    }

    override suspend fun getTask(id: String): Result<Task> {
        return Result.catch { localTaskDataSource.get(id) }
    }

    override fun getCompletedTasks(): Flow<Result<List<Task>>> {
        return localTaskDataSource.getCompleted().toResultFlow()
    }

    override suspend fun updateTasks(tasks: List<Task>): Result<Unit> {
        val params = TaskModificationParameters(tasks)
        return params teePipeTo
                localTaskDataSource::updateAll then
                remoteTaskDataSource::updateAll
    }

    override suspend fun addTasks(tasks: List<Task>): Result<Unit> {
        val params = TaskModificationParameters(tasks)
        return params teePipeTo
                localTaskDataSource::addAll then
                remoteTaskDataSource::addAll
    }

    override suspend fun deleteTasks(tasks: List<Task>): Result<Unit> {
        val params = TaskModificationParameters(tasks)
        return params teePipeTo
                localTaskDataSource::deleteAll then
                remoteTaskDataSource::deleteAll
    }

    companion object {
        private const val SYNC_TIME_PREFERENCE_KEY = "last_sync_time"
    }
}