package com.yaroslavgamayunov.toodoo.util

import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

object TimeUtils {
    val minZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(Long.MIN_VALUE),
            ZoneId.systemDefault()
        )

    val maxZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(Long.MAX_VALUE),
            ZoneId.systemDefault()
        )

    /**
     *  Returns the closest moment in future relatively to [Instant.now]
     *  when time of day equals to [timeOfDay]
     */
    fun getClosestTimeOfDay(timeOfDay: LocalTime): Instant {
        val currentTime = ZonedDateTime.now().toLocalTime()
        val now = Instant.now()

        val difference =
            (timeOfDay.toSecondOfDay() - currentTime.toSecondOfDay()).toLong().absoluteValue

        return if (currentTime.isBefore(timeOfDay)) {
            now.plusSeconds(difference)
        } else {
            now.plus(1, ChronoUnit.DAYS).minusSeconds(difference)
        }
    }
}

// Applied to a local unix time and returns ZonedDateTime
fun Long.localToZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    ZonedDateTime.of(
        LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.ofHours(0)),
        zoneId
    )

fun LocalDateTime.isStartOfDay(): Boolean {
    return this.toLocalTime().second == 0
}

fun Instant.taskScheduleMode(zoneId: ZoneId = ZoneId.systemDefault()): TaskScheduleMode {
    if (this == TimeUtils.maxZonedDateTime.toInstant()) {
        return TaskScheduleMode.Unspecified
    }
    val localDateTime = ZonedDateTime.ofInstant(this, zoneId).toLocalDateTime()
    if (localDateTime.isStartOfDay()) {
        return TaskScheduleMode.ByDate
    }
    return TaskScheduleMode.ByTime
}