package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun taskDatabase(): TaskDatabase
}