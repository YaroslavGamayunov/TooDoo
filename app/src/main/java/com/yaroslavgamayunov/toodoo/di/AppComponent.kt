package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import com.yaroslavgamayunov.toodoo.ui.MainActivity
import com.yaroslavgamayunov.toodoo.ui.TaskEditFragment
import com.yaroslavgamayunov.toodoo.ui.mainpage.MainPageFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface AppComponent {
    fun inject(application: TooDooApplication)
    fun inject(activity: MainActivity)
    fun inject(fragment: MainPageFragment)
    fun inject(fragment: TaskEditFragment)
}