package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.yaroslavgamayunov.toodoo.di.ApplicationContext
import com.yaroslavgamayunov.toodoo.di.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class NetworkUtils @Inject constructor(@ApplicationContext private val context: Context) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        val activeNetwork = connectivityManager?.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}