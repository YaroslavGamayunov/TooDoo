package com.yaroslavgamayunov.toodoo.di

import com.yaroslavgamayunov.toodoo.ui.MainActivity
import com.yaroslavgamayunov.toodoo.ui.TaskEditFragment
import com.yaroslavgamayunov.toodoo.ui.mainpage.MainPageFragment
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ViewModelModule::class])
@Singleton
interface ViewModelComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: MainPageFragment)
    fun inject(fragment: TaskEditFragment)

    fun viewModelFactory(): TooDooViewModelFactory
}