package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskRoomEntity::class, TaskState::class], version = 1, exportSchema = false)
@TypeConverters(
    TimeConverter::class,
    PriorityConverter::class,
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskStateDao(): TaskStateDao

    companion object {
        const val NAME = "task_db"
    }
}