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
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

interface TaskRepository {
    suspend fun synchronizeLocalAndRemote()
    val lastSynchronizationTime: Instant
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTask(id: String): Task
    fun getCompletedTasks(): Flow<List<Task>>
    fun getCountOfCompletedTasks(): Flow<Int>
    fun getUncompletedTasks(): Flow<List<Task>>
    fun getCountOfDailyTasks(): Flow<Int>

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
    val externalScope: CoroutineScope,
    @ApplicationContext
    val context: Context
) : TaskRepository {

    override var lastSynchronizationTime: Instant
        get() {
            return Instant.ofEpochSecond(
                context.getSharedPreferences(SYNC_TIME_SP, Context.MODE_PRIVATE).getLong(
                    LAST_SYNC_TIME_KEY, 0
                )
            )
        }
        set(value) {
            context.getSharedPreferences(SYNC_TIME_SP, Context.MODE_PRIVATE).edit {
                putLong(LAST_SYNC_TIME_KEY, value.epochSecond)
            }
        }

    override suspend fun synchronizeLocalAndRemote() {
        externalScope.launch {
            val lastSync = lastSynchronizationTime
            lastSynchronizationTime = Instant.now()
            localTaskDataSource.synchronizeWith(remoteTaskDataSource, lastSync)
            remoteTaskDataSource.synchronizeWith(localTaskDataSource, lastSync)
        }.join()
    }

    override fun getAllTasks(): Flow<List<Task>> = localTaskDataSource.getAll()

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

    override suspend fun updateTasks(tasks: List<Task>) {
        val timeOfUpdate = Instant.now()
        externalScope.launch { remoteTaskDataSource.updateAll(tasks, timeOfUpdate) }
        externalScope.launch { localTaskDataSource.updateAll(tasks, timeOfUpdate) }.join()
    }

    override suspend fun addTasks(tasks: List<Task>) {
        val timeOfAdd = Instant.now()
        externalScope.launch { remoteTaskDataSource.addAll(tasks, timeOfAdd) }
        externalScope.launch { localTaskDataSource.addAll(tasks, timeOfAdd) }.join()
    }

    override suspend fun deleteTasks(tasks: List<Task>) {
        externalScope.launch { remoteTaskDataSource.deleteAll(tasks) }
        externalScope.launch { localTaskDataSource.deleteAll(tasks) }.join()
    }

    companion object {
        const val SYNC_TIME_SP = "sync_time_shared_preferences"
        const val LAST_SYNC_TIME_KEY = "last_sync_time"
    }
}