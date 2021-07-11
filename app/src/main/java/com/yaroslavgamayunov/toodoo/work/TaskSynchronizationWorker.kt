package com.yaroslavgamayunov.toodoo.work

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.*
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.domain.SynchronizeTasksUseCase
import com.yaroslavgamayunov.toodoo.domain.common.Result as UseCaseResult
import com.yaroslavgamayunov.toodoo.util.createNotificationChannel
import java.time.Duration
import java.util.concurrent.TimeUnit

class TaskSynchronizationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val synchronizeTasksUseCase: SynchronizeTasksUseCase
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        return when (synchronizeTasksUseCase.invoke(Unit)) {
            is UseCaseResult.Success -> Result.success()
            else -> Result.retry()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notificationManager =
            applicationContext.getSystemService<NotificationManager>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager?.createNotificationChannel(
                channelName = applicationContext.getString(R.string.task_sync_notification_channel_title),
                channelId = SYNC_NOTIFICATION_CHANNEL_ID
            )
        }

        val title = applicationContext.getString(R.string.task_sync_notification_title)
        val notification: Notification = NotificationCompat
            .Builder(applicationContext, SYNC_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle(title)
            .build()
        return ForegroundInfo(SYNC_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val WORKER_TAG = "task_synchronization_worker"
        private const val SYNC_NOTIFICATION_CHANNEL_ID =
            "task_synchronization_channel_id"
        private const val SYNC_NOTIFICATION_ID = 1

        private val synchronizationPeriod = Duration.ofHours(8)

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request =
                PeriodicWorkRequestBuilder<TaskSynchronizationWorker>(synchronizationPeriod)
                    .addTag(WORKER_TAG)
                    .setInitialDelay(synchronizationPeriod.seconds, TimeUnit.SECONDS)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}