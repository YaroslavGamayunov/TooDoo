package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskRoomEntity>)

    @Query("SELECT tasks.* FROM tasks JOIN task_states ON tasks.task_id=task_states.task_id ORDER BY deadline ASC, priority DESC, created_at ASC")
    fun getAll(): Flow<List<TaskRoomEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :id LIMIT 1")
    suspend fun getTask(id: String): TaskRoomEntity

    @Query("SELECT * FROM task_states INNER JOIN tasks ON task_states.task_id=tasks.task_id")
    suspend fun getAllTaskStates(): List<TaskState>

    @Query("SELECT * FROM tasks WHERE deadline >= :minDeadline AND deadline < :maxDeadline")
    fun getAllInTimeRange(
        minDeadline: Instant,
        maxDeadline: Instant,
    ): Flow<List<TaskRoomEntity>>

    @Delete
    suspend fun deleteAll(tasks: List<TaskRoomEntity>)

    @Query("UPDATE tasks SET completed = :completed WHERE task_id = :taskId")
    suspend fun setCompleted(taskId: String, completed: Boolean)

    @Query("SELECT * FROM tasks WHERE completed = 1")
    fun getAllCompleted(): Flow<List<TaskRoomEntity>>
}
