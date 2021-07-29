package com.yaroslavgamayunov.toodoo.data.api

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.yaroslavgamayunov.toodoo.data.model.TaskPriority

class TaskPriorityAdapter : TypeAdapter<TaskPriority>() {

    override fun write(out: JsonWriter?, value: TaskPriority?) {
        val serializedPriority =
            taskPriorityToString.find { it.first == value }?.second ?: throw JsonParseException(
                "TaskPriority value $value can't be serialized"
            )
        out?.value(serializedPriority)
    }

    override fun read(`in`: JsonReader?): TaskPriority {
        val serializedPriority = `in`?.nextString()

        return taskPriorityToString.find { it.second == serializedPriority }?.first
            ?: throw JsonParseException(
                "Serialized value of TaskPriority `${serializedPriority}` can't be deserialized"
            )
    }

    companion object {
        private val taskPriorityToString = listOf(
            TaskPriority.None to "low",
            TaskPriority.Low to "basic",
            TaskPriority.High to "important"
        )
    }
}
