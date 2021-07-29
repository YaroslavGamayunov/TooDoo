package com.yaroslavgamayunov.toodoo.data.model

import com.yaroslavgamayunov.toodoo.domain.entities.Task
import java.time.Instant

data class TaskWithTimestamps(
    val data: Task,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
)

val TaskWithTimestamps.isDeleted: Boolean
    get() = this.deletedAt != null
