package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity
data class Timestamps(
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant
)
