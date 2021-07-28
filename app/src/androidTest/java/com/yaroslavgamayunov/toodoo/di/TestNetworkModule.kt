package com.yaroslavgamayunov.toodoo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yaroslavgamayunov.toodoo.BuildConfig
import com.yaroslavgamayunov.toodoo.TestConstants
import com.yaroslavgamayunov.toodoo.data.api.TaskApiService
import com.yaroslavgamayunov.toodoo.data.api.TaskPriorityAdapter
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.time.Duration

@Module
class TestNetworkModule {
    @Provides
    @ApplicationScoped
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @ApplicationScoped
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TaskPriority::class.java, TaskPriorityAdapter())
            .create()
    }

    @Provides
    @ApplicationScoped
    fun provideTaskApiService(client: OkHttpClient, gson: Gson): TaskApiService {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(TestConstants.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TaskApiService::class.java)
    }
}