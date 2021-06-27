package com.yaroslavgamayunov.toodoo.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import java.time.Instant

@ProvidedTypeConverter
class TimeConverter {
    @TypeConverter
    fun instantToLong(instant: Instant?): Long? {
        return instant?.epochSecond
    }

    @TypeConverter
    fun longToInstant(epochSecond: Long?): Instant? {
        return epochSecond?.let { Instant.ofEpochSecond(it) }
    }
}

@ProvidedTypeConverter
class PriorityConverter {
    @TypeConverter
    fun priorityToInt(priority: TaskPriority?): Int? {
        return priority?.level
    }

    @TypeConverter
    fun intToPriority(level: Int?): TaskPriority? {
        return TaskPriority.values().find { it.level == level }
    }
}

@ProvidedTypeConverter
class ScheduleModeConverter {
    @TypeConverter
    fun scheduleModeToInt(scheduleMode: TaskScheduleMode?): Int? {
        return scheduleMode?.id
    }

    @TypeConverter
    fun intToScheduleMode(mode: Int?): TaskScheduleMode? {
        return TaskScheduleMode.values().find { it.id == mode }
    }
}