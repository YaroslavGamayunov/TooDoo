package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.di.ApplicationCoroutineScope
import com.yaroslavgamayunov.toodoo.di.LocalDataSource
import com.yaroslavgamayunov.toodoo.di.RemoteDataSource
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.util.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

interface TaskRepository {
    suspend fun synchronizeLocalAndRemote()
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTask(id: String): Task
    fun getCompletedTasks(): Flow<List<Task>>
    fun getCountOfCompletedTasks(): Flow<Int>
    fun getUncompletedTasks(): Flow<List<Task>>
    fun getCountOfDailyTasks(): Flow<Int>

    suspend fun updateTask(task: Task)
    suspend fun addTask(task: Task)
    suspend fun deleteTask(task: Task)
}

class DefaultTaskRepository @Inject constructor(
    @LocalDataSource
    private val localTaskDataSource: TaskDataSource,
    @RemoteDataSource
    private val remoteTaskDataSource: TaskDataSource,
    private val networkUtils: NetworkUtils,
    @ApplicationCoroutineScope
    val externalScope: CoroutineScope
) : TaskRepository {

    override suspend fun synchronizeLocalAndRemote() {
        externalScope.launch {
            if (networkUtils.isNetworkAvailable()) {
                localTaskDataSource.synchronizeWith(remoteTaskDataSource)
                remoteTaskDataSource.synchronizeWith(localTaskDataSource)
            }
        }.join()
    }

    override fun getAllTasks(): Flow<List<Task>> = localTaskDataSource.getAll().filterNotNull()

    override suspend fun getTask(id: String): Task = localTaskDataSource.get(id)

    override fun getCompletedTasks(): Flow<List<Task>> =
        localTaskDataSource.getAll().map { tasks -> tasks.filter { it.isCompleted } }

    override fun getCountOfCompletedTasks(): Flow<Int> =
        localTaskDataSource.getAll().map { tasks -> tasks.count { it.isCompleted } }

    override fun getUncompletedTasks(): Flow<List<Task>> =
        localTaskDataSource.getAll().map { tasks -> tasks.filter { it.isCompleted } }

    override fun getCountOfDailyTasks(): Flow<Int> {
        val currentDayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val currentDayEnd = currentDayStart.plus(1, ChronoUnit.DAYS)

        return localTaskDataSource.getAll().map { tasks ->
            tasks.count {
                !it.isCompleted and
                        it.deadline.isAfter(currentDayStart) and
                        it.deadline.isBefore(currentDayEnd)
            }
        }
    }

    override suspend fun updateTask(task: Task) {
        val timeOfUpdate = Instant.now()
        externalScope.launch { remoteTaskDataSource.update(task, timeOfUpdate) }
        externalScope.launch { localTaskDataSource.update(task, timeOfUpdate) }.join()
    }

    override suspend fun addTask(task: Task) {
        val timeOfAdd = Instant.now()
        externalScope.launch { remoteTaskDataSource.add(task, timeOfAdd) }
        externalScope.launch { localTaskDataSource.add(task, timeOfAdd) }.join()
    }

    override suspend fun deleteTask(task: Task) {
        externalScope.launch { remoteTaskDataSource.delete(task) }
        externalScope.launch { localTaskDataSource.delete(task) }.join()
    }
}