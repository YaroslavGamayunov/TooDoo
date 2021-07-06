package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @ApplicationScoped
    @Provides
    fun provideTaskDatabase(context: Context): TaskDatabase {
        return TaskDatabase.build(context)
    }
}