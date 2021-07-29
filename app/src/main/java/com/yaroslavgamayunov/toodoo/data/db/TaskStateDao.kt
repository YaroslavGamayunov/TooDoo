package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.*

@Dao
interface TaskStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(states: List<TaskState>)

    @Query("SELECT * FROM task_states")
    fun getAll(): List<TaskState>

    @Update(entity = TaskState::class)
    fun updateCreatedAt(updates: List<TaskStateUpdate.CreatedAt>)

    @Update(entity = TaskState::class)
    fun updateDeletedAt(updates: List<TaskStateUpdate.DeletedAt>)

    @Update(entity = TaskState::class)
    fun updateUpdatedAt(updates: List<TaskStateUpdate.UpdatedAt>)
}