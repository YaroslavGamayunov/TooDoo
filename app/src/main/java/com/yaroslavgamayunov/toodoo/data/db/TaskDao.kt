package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskRoomEntity>)

    @Query("SELECT * FROM tasks ORDER BY deadline ASC, priority DESC")
    fun getAll(): Flow<List<TaskRoomEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :id LIMIT 1")
    suspend fun getTask(id: String): TaskRoomEntity

    @Query("SELECT * FROM task_states INNER JOIN tasks ON task_states.task_id=tasks.task_id")
    suspend fun getAllTaskStates(): List<TaskState>

    @Delete
    suspend fun deleteAll(tasks: List<TaskRoomEntity>)

    @Query("UPDATE tasks SET completed = :completed WHERE task_id = :taskId")
    suspend fun setCompleted(taskId: String, completed: Boolean)
}
