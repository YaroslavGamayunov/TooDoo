package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import androidx.room.Room
import com.yaroslavgamayunov.toodoo.data.db.PriorityConverter
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.db.TimeConverter
import dagger.Module
import dagger.Provides


@Module(includes = [AppModuleBinds::class])
class TestAppModule {
    @ApplicationScoped
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        context.deleteDatabase(TaskDatabase.NAME)
        return Room.databaseBuilder(context, TaskDatabase::class.java, TaskDatabase.NAME)
            .addTypeConverter(TimeConverter())
            .addTypeConverter(PriorityConverter())
            .createFromAsset("database/task_db")
            .build()
    }
}