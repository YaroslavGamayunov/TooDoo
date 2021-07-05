package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.db.TaskDao
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun getTask(id: Int): TaskEntity
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    fun getCountOfCompletedTasks(): Flow<Int>
    fun getUncompletedTasks(): Flow<List<TaskEntity>>
    fun getCountOfDailyTasks(): Flow<Int>

    suspend fun setCompleted(task: TaskEntity, isCompleted: Boolean)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    suspend fun deleteTask(task: TaskEntity)
}

class DefaultTaskRepository @Inject constructor(
    database: TaskDatabase,
    @Named("applicationCoroutineScope")
    val applicationCoroutineScope: CoroutineScope
) : TaskRepository {
    private val taskDao: TaskDao = database.taskDao()
    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAll()

    override suspend fun getTask(id: Int): TaskEntity = taskDao.getTask(id)

    override fun getCompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getAll(completed = true)

    override fun getCountOfCompletedTasks(): Flow<Int> {
        return taskDao.getCountOfTasks(completed = true)
    }

    override fun getUncompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getAll(completed = false)

    override fun getCountOfDailyTasks(): Flow<Int> {
        val currentDayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()
        val currentDayEnd = currentDayStart.plus(1, ChronoUnit.DAYS)

        return taskDao.getCountOfTasks(
            completed = false,
            minDeadlineTime = currentDayStart,
            maxDeadlineTime = currentDayEnd
        )
    }

    override suspend fun setCompleted(task: TaskEntity, isCompleted: Boolean) {
        applicationCoroutineScope.launch {
            taskDao.setCompleted(task.taskId, completed = isCompleted)
        }.join()
    }

    override suspend fun insertTasks(tasks: List<TaskEntity>) {
        applicationCoroutineScope.launch {
            taskDao.insertAll(tasks)
        }.join()
    }

    override suspend fun deleteTask(task: TaskEntity) {
        applicationCoroutineScope.launch {
            taskDao.delete(task)
        }.join()
    }
}