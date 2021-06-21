package com.yaroslavgamayunov.toodoo

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.yaroslavgamayunov.toodoo.di.*

class TooDooApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    val viewModelComponent: ViewModelComponent by lazy {
        DaggerViewModelComponent.builder().build()
    }
}