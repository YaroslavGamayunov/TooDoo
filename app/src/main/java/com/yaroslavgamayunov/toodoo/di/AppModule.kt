package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: TooDooApplication) {
    @Singleton
    @Provides
    fun provideApplication(): TooDooApplication = application

    @Singleton
    @Provides
    fun provideTaskDatabase(): TaskDatabase {
        return TaskDatabase.build(application)
    }
}