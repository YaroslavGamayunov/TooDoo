package com.yaroslavgamayunov.toodoo.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.yaroslavgamayunov.toodoo.domain.SynchronizeTasksUseCase
import dagger.Lazy
import javax.inject.Inject

class TaskSynchronizationWorkerFactory @Inject constructor(
    private val synchronizeTasksUseCase: SynchronizeTasksUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == TaskSynchronizationWorker::class.java.name) {
            TaskSynchronizationWorker(appContext, workerParameters, synchronizeTasksUseCase)
        } else null
    }
}