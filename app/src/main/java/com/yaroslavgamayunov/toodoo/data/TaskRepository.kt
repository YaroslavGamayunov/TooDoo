package com.yaroslavgamayunov.toodoo.data

import android.content.Context
import androidx.core.content.edit
import com.yaroslavgamayunov.toodoo.di.ApplicationContext
import com.yaroslavgamayunov.toodoo.di.ApplicationCoroutineScope
import com.yaroslavgamayunov.toodoo.di.LocalDataSource
import com.yaroslavgamayunov.toodoo.di.RemoteDataSource
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

interface TaskRepository {
    suspend fun refreshData()

    fun getAllTasks(): Flow<List<Task>>
    fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>>
    suspend fun getTask(id: String): Task

    fun getCompletedTasks(): Flow<List<Task>>

    suspend fun updateTasks(tasks: List<Task>)
    suspend fun addTasks(tasks: List<Task>)
    suspend fun deleteTasks(tasks: List<Task>)
}

class DefaultTaskRepository @Inject constructor(
    @LocalDataSource
    private val localTaskDataSource: TaskDataSource,
    @RemoteDataSource
    private val remoteTaskDataSource: TaskDataSource,
    @ApplicationCoroutineScope
    private val applicationScope: CoroutineScope,
    @ApplicationContext
    private val context: Context
) : TaskRepository {

    private var lastSynchronizationTime: Long
            by PreferenceDelegate(context, SYNC_TIME_SP_KEY, 0)

    override suspend fun refreshData() {
        applicationScope.launch {
            val lastSync = Instant.ofEpochSecond(lastSynchronizationTime)
            lastSynchronizationTime = Instant.now().epochSecond
            localTaskDataSource.synchronizeWith(remoteTaskDataSource, lastSync)
            remoteTaskDataSource.synchronizeWith(localTaskDataSource, lastSync)
        }.join()
    }

    override fun getAllTasks(): Flow<List<Task>> = localTaskDataSource.getAll()

    override fun getAllInTimeRange(minDeadline: Instant, maxDeadline: Instant): Flow<List<Task>> {
        return localTaskDataSource.getAllInTimeRange(minDeadline, maxDeadline)
    }

    override suspend fun getTask(id: String): Task = localTaskDataSource.get(id)

    override fun getCompletedTasks(): Flow<List<Task>> =
        localTaskDataSource.getAll().map { tasks -> tasks.filter { it.isCompleted } }

    override suspend fun updateTasks(tasks: List<Task>) {
        val timeOfUpdate = Instant.now()
        applicationScope.launch { remoteTaskDataSource.updateAll(tasks, timeOfUpdate) }
        applicationScope.launch { localTaskDataSource.updateAll(tasks, timeOfUpdate) }.join()
    }

    override suspend fun addTasks(tasks: List<Task>) {
        val timeOfAdd = Instant.now()
        applicationScope.launch { remoteTaskDataSource.addAll(tasks, timeOfAdd) }
        applicationScope.launch { localTaskDataSource.addAll(tasks, timeOfAdd) }.join()
    }

    override suspend fun deleteTasks(tasks: List<Task>) {
        applicationScope.launch { remoteTaskDataSource.deleteAll(tasks) }
        applicationScope.launch { localTaskDataSource.deleteAll(tasks) }.join()
    }

    companion object {
        private const val SYNC_TIME_SP_KEY = "last_sync_time"
    }
}