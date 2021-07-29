package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.util.mapResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetCountOfDailyTasksUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository,
) : UseCase<Unit, Int>(dispatcher) {
    override suspend fun execute(params: Unit): Result<Int> {
        val currentDayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val currentDayEnd = currentDayStart.plus(1, ChronoUnit.DAYS)

        return taskRepository
            .getAllInTimeRange(currentDayStart.toInstant(), currentDayEnd.toInstant())
            .mapResult { it.size }
            .first()
    }
}