package com.yaroslavgamayunov.toodoo.data.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TaskApiService {
    @GET("/tasks/")
    suspend fun getAllTasks(): List<TaskApiEntity>

    @POST("/tasks/")
    suspend fun addTask(@Body taskApiEntity: TaskApiEntity): TaskApiEntity

    @PUT("/tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body taskApiEntity: TaskApiEntity
    ): TaskApiEntity

    @DELETE("/tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: String): TaskApiEntity

    @PUT("/tasks/")
    suspend fun synchronizeAllChanges(@Body request: TaskSynchronizationRequest): List<TaskApiEntity>

    companion object {
        private const val BASE_URL = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net"
        fun create(okHttpClient: OkHttpClient, gson: Gson): TaskApiService {
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(TaskApiService::class.java)
        }
    }
}