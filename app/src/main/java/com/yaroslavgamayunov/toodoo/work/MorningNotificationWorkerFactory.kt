package com.yaroslavgamayunov.toodoo.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.yaroslavgamayunov.toodoo.domain.GetCountOfDailyTasksUseCase
import javax.inject.Inject

class MorningNotificationWorkerFactory @Inject constructor(
    private val getCountOfDailyTasksUseCase: GetCountOfDailyTasksUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == MorningNotificationWorker::class.java.name) {
            MorningNotificationWorker(appContext, workerParameters, getCountOfDailyTasksUseCase)
        } else null
    }
}