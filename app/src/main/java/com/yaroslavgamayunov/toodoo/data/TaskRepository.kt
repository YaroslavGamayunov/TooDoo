package com.yaroslavgamayunov.toodoo.data

import com.yaroslavgamayunov.toodoo.data.db.TaskDao
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.db.TaskEntity
import javax.inject.Inject

interface TaskRepository {
    suspend fun getAllTasks(): List<TaskEntity>
    suspend fun getCompletedTasks(): List<TaskEntity>
    suspend fun getUncompletedTasks(): List<TaskEntity>

    suspend fun setCompleted(task: TaskEntity)
    suspend fun setUncompleted(task: TaskEntity)

    suspend fun insertTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
}

class DefaultTaskRepository @Inject constructor(
    database: TaskDatabase
) : TaskRepository {
    private val taskDao: TaskDao = database.taskDao()
    override suspend fun getAllTasks(): List<TaskEntity> = taskDao.getAll()

    override suspend fun getCompletedTasks(): List<TaskEntity> =
        taskDao.getAll(completed = true)

    override suspend fun getUncompletedTasks(): List<TaskEntity> =
        taskDao.getAll(completed = false)

    override suspend fun setCompleted(task: TaskEntity) {
        taskDao.setCompleted(task.taskId, completed = true)
    }

    override suspend fun setUncompleted(task: TaskEntity) {
        taskDao.setCompleted(task.taskId, completed = false)
    }

    override suspend fun insertTask(task: TaskEntity) {
        taskDao.insertAll(listOf(task))
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }
}