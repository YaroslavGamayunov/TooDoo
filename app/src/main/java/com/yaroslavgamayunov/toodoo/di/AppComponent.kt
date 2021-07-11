package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.ui.MainActivity
import com.yaroslavgamayunov.toodoo.ui.TaskEditFragment
import com.yaroslavgamayunov.toodoo.ui.mainpage.MainPageFragment
import dagger.BindsInstance
import dagger.Component


@ApplicationScoped
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        WorkerModule::class,
        NetworkModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Builder {
        fun create(@BindsInstance @ApplicationContext context: Context): AppComponent
    }

    fun inject(application: TooDooApplication)
    fun inject(activity: MainActivity)
    fun inject(fragment: MainPageFragment)
    fun inject(fragment: TaskEditFragment)
}