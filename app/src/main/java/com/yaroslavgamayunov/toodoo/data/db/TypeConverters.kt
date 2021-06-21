package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import java.time.Instant

@ProvidedTypeConverter
class TimeConverter {
    @TypeConverter
    fun InstantToLong(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun LongToInstant(epochMilli: Long?): Instant? {
        return epochMilli?.let { Instant.ofEpochMilli(it) }
    }
}

@ProvidedTypeConverter
class PriorityConverter {
    @TypeConverter
    fun PriorityToInt(priority: TaskPriority?): Int? {
        return priority?.level
    }

    @TypeConverter
    fun IntToPriority(level: Int?): TaskPriority? {
        return TaskPriority.values().find { it.level == level }
    }
}