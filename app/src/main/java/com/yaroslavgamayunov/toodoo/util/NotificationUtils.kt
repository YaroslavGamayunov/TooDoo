package com.yaroslavgamayunov.toodoo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: CharSequence,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    val notificationChannel = NotificationChannel(
        channelId,
        channelName,
        importance
    )

    createNotificationChannel(notificationChannel)
}