package com.yaroslavgamayunov.toodoo.data.api

import retrofit2.http.*

interface TaskApiService {
    @GET("/tasks/")
    suspend fun getAllTasks(): List<TaskApiEntity>

    @POST("/tasks/")
    suspend fun addTask(@Body taskApiEntity: TaskApiEntity): TaskApiEntity

    @PUT("/tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body taskApiEntity: TaskApiEntity,
    ): TaskApiEntity

    @DELETE("/tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: String): TaskApiEntity

    @PUT("/tasks/")
    suspend fun synchronizeAllChanges(@Body request: TaskSynchronizationRequest): List<TaskApiEntity>

    companion object {
        const val BASE_URL = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net"
    }
}