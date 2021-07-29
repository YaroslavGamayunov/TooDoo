package com.yaroslavgamayunov.toodoo.domain.entities

import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.util.TimeUtils
import com.yaroslavgamayunov.toodoo.util.isStartOfDay
import java.time.ZonedDateTime
import java.util.*

data class Task(
    val taskId: String,
    val description: String,
    val isCompleted: Boolean,
    val deadline: ZonedDateTime,
    val priority: TaskPriority
)

val Task.scheduleMode: TaskScheduleMode
    get() {
        if (this.deadline.toEpochSecond() == TimeUtils.maxZonedDateTime.toEpochSecond()) {
            return TaskScheduleMode.Unspecified
        }
        val localDateTime = deadline.toLocalDateTime()
        if (localDateTime.isStartOfDay()) {
            return TaskScheduleMode.ByDate
        }
        return TaskScheduleMode.ByTime
    }

enum class TaskScheduleMode {
    ByTime,
    ByDate,
    Unspecified
}
