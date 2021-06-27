package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
@Entity
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks WHERE completed = :completed")
    fun getAll(completed: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :id LIMIT 1")
    suspend fun getTask(id: Int): TaskEntity

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET completed = :completed WHERE task_id = :taskId")
    suspend fun setCompleted(taskId: Int, completed: Boolean)
}
