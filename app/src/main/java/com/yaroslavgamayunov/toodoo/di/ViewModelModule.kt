package com.yaroslavgamayunov.toodoo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yaroslavgamayunov.toodoo.ui.viewmodel.MainPageViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TaskEditViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val key: KClass<out ViewModel>)

@Module(includes = [UseCaseModule::class])
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainPageViewModel::class)
    abstract fun bindMainPageViewModel(model: MainPageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaskEditViewModel::class)
    abstract fun bindTaskEditViewModel(model: TaskEditViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: TooDooViewModelFactory): ViewModelProvider.Factory
}