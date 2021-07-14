package com.yaroslavgamayunov.toodoo.util

import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

object TimeUtils {
    val minZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(Int.MIN_VALUE.toLong()),
            ZoneId.systemDefault()
        )

    val maxZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(Int.MAX_VALUE.toLong()),
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