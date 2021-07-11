package com.yaroslavgamayunov.toodoo.data.api

import com.google.gson.annotations.SerializedName

data class TaskSynchronizationRequest(
    @SerializedName("deleted")
    val deleted: List<String> = listOf(),
    @SerializedName("other")
    val other: List<TaskApiEntity> = listOf()
)
