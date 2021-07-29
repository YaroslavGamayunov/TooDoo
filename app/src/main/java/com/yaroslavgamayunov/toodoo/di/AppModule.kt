package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yaroslavgamayunov.toodoo.data.DefaultPreferenceHelper
import com.yaroslavgamayunov.toodoo.data.PreferenceHelper
import com.yaroslavgamayunov.toodoo.data.db.PriorityConverter
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.data.db.TimeConverter
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [AppModuleBinds::class])
class AppModule {
    @ApplicationScoped
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(context, TaskDatabase::class.java, TaskDatabase.NAME)
            .addTypeConverter(TimeConverter())
            .addTypeConverter(PriorityConverter())
            .fallbackToDestructiveMigration().build()
    }
}

@Module
interface AppModuleBinds {
    @ApplicationScoped
    @Binds
    fun bindPreferenceHelper(preferenceHelper: DefaultPreferenceHelper): PreferenceHelper
}