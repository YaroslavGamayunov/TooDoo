package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(modules = [
    TestAppModule::class, // Test module
    RepositoryModule::class,
    CoroutineModule::class,
    ViewModelModule::class,
    WorkerModule::class,
    TestNetworkModule::class, // Test module
    DataSyncModule::class
])
@ApplicationScoped
interface TestAppComponent : AppComponent {
    @Component.Factory
    interface Builder {
        fun create(@BindsInstance @ApplicationContext context: Context): TestAppComponent
    }
}