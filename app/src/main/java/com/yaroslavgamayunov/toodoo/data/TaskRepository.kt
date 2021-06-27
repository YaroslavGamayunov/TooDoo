package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.db.TaskDao
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TaskRepository {
    suspend fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun getTask(id: Int): TaskEntity
    suspend fun getCompletedTasks(): Flow<List<TaskEntity>>
    suspend fun getNumberOfCompletedTasks(): Flow<Int>
    suspend fun getUncompletedTasks(): Flow<List<TaskEntity>>

    suspend fun setCompleted(task: TaskEntity, isCompleted: Boolean)

    suspend fun insertTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
}

class DefaultTaskRepository @Inject constructor(
    database: TaskDatabase
) : TaskRepository {
    private val taskDao: TaskDao = database.taskDao()
    override suspend fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAll()

    override suspend fun getTask(id: Int): TaskEntity = taskDao.getTask(id)

    override suspend fun getCompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getAll(completed = true)

    override suspend fun getNumberOfCompletedTasks(): Flow<Int> {
        return taskDao.getNumberOfCompleted()
    }

    override suspend fun getUncompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getAll(completed = false)

    override suspend fun setCompleted(task: TaskEntity, isCompleted: Boolean) {
        taskDao.setCompleted(task.taskId, completed = isCompleted)
    }

    override suspend fun insertTask(task: TaskEntity) {
        taskDao.insertAll(listOf(task))
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }
}