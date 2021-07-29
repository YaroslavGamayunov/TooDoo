package com.yaroslavgamayunov.toodoo

import com.yaroslavgamayunov.toodoo.data.model.TaskPriority
import com.yaroslavgamayunov.toodoo.data.model.TaskWithTimestamps
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

object SampleData {
    val times = listOf(ZonedDateTime.ofInstant(Instant.ofEpochSecond(10), ZoneOffset.UTC),
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(20), ZoneOffset.UTC),
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(30), ZoneOffset.UTC),
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(30), ZoneOffset.UTC),
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(30), ZoneOffset.UTC))

    val tasks = listOf(
        Task("1", "", false, times[0], priority = TaskPriority.None),
        Task("3", "", false, times[4], priority = TaskPriority.High),
        Task("2", "", false, times[1], priority = TaskPriority.None),
        Task("5", "", false, times[3], priority = TaskPriority.None),
        Task("4", "", false, times[2], priority = TaskPriority.Low)
    )

    val tasksWithTimestamps = tasks.map {
        TaskWithTimestamps(data = it,
            createdAt = Instant.ofEpochSecond(0),
            updatedAt = Instant.ofEpochSecond(0))
    }
}