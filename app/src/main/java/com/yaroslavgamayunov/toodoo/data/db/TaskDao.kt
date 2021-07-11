package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskRoomEntity>)

    @Query("SELECT * FROM tasks WHERE completed = :completed ORDER BY deadline ASC, priority DESC")
    fun getAll(completed: Boolean): Flow<List<TaskRoomEntity>>

    @Query("SELECT * FROM tasks ORDER BY deadline ASC, priority DESC, created_at ASC")
    fun getAll(): Flow<List<TaskRoomEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :id LIMIT 1")
    suspend fun getTask(id: String): TaskRoomEntity

    @Delete
    suspend fun deleteAll(tasks: List<TaskRoomEntity>)

    @Query("UPDATE tasks SET completed = :completed WHERE task_id = :taskId")
    suspend fun setCompleted(taskId: String, completed: Boolean)

    @Query("SELECT task_id, created_at, updated_at from tasks WHERE task_id IN(:taskIds)")
    suspend fun getTimestamps(taskIds: List<String>): List<Timestamps>

    @Query("SELECT COUNT(task_id) FROM tasks WHERE completed = :completed AND deadline >= :minDeadlineTime AND deadline < :maxDeadlineTime")
    fun getCountOfTasks(
        completed: Boolean,
        minDeadlineTime: Instant = Instant.MIN,
        maxDeadlineTime: Instant = Instant.MAX
    ): Flow<Int>
}
