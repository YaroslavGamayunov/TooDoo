package com.yaroslavgamayunov.toodoo.di

import android.content.Context
import com.yaroslavgamayunov.toodoo.data.DefaultPreferenceHelper
import com.yaroslavgamayunov.toodoo.data.PreferenceHelper
import com.yaroslavgamayunov.toodoo.data.db.TaskDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [AppModuleBinds::class])
class AppModule {
    @ApplicationScoped
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.build(context)
    }
}

@Module
interface AppModuleBinds {
    @ApplicationScoped
    @Binds
    fun bindPreferenceHelper(preferenceHelper: DefaultPreferenceHelper): PreferenceHelper
}