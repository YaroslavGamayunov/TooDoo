package com.yaroslavgamayunov.toodoo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(TimeConverter::class, PriorityConverter::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private const val name = "task_db"
        fun build(context: Context): TaskDatabase {
            return Room.databaseBuilder(context, TaskDatabase::class.java, name)
                .fallbackToDestructiveMigration().build()
        }
    }
}