package com.yaroslavgamayunov.toodoo.domain

import com.yaroslavgamayunov.toodoo.data.TaskRepository
import com.yaroslavgamayunov.toodoo.di.IoDispatcher
import com.yaroslavgamayunov.toodoo.domain.common.UseCase
import com.yaroslavgamayunov.toodoo.util.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SynchronizeTasksUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val networkUtils: NetworkUtils,
    private val taskRepository: TaskRepository
) : UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(params: Unit): Boolean {
        return if (networkUtils.isNetworkAvailable()) {
            taskRepository.refreshData()
            true
        } else false
    }
}