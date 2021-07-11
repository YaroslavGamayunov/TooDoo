package com.yaroslavgamayunov.toodoo.data.api

import com.google.gson.annotations.SerializedName
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority

data class TaskApiEntity(
    @SerializedName("id")
    val taskId: String,
    @SerializedName("text")
    val description: String,
    @SerializedName("importance")
    val priority: TaskPriority,
    @SerializedName("done")
    val isCompleted: Boolean,
    val deadline: Long,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("updated_at")
    val updatedAt: Long
)