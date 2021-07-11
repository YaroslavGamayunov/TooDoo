package com.yaroslavgamayunov.toodoo.data.model

enum class TaskScheduleMode(val id: Int) {
    ExactTime(0),
    NotExactTime(1),
    Unspecified(2)
}
