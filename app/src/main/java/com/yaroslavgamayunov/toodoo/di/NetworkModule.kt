package com.yaroslavgamayunov.toodoo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yaroslavgamayunov.toodoo.BuildConfig
import com.yaroslavgamayunov.toodoo.data.api.TaskApiService
import com.yaroslavgamayunov.toodoo.data.api.TaskPriorityAdapter
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Duration

@Module
class NetworkModule {
    @Provides
    @ApplicationScoped
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.TASK_API_TOKEN}")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(CLIENT_TIMEOUT)
            .readTimeout(CLIENT_TIMEOUT)
            .writeTimeout(CLIENT_TIMEOUT)
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
        return TaskApiService.create(client, gson)
    }

    companion object {
        val CLIENT_TIMEOUT: Duration = Duration.ofSeconds(30)
    }
}