package com.yaroslavgamayunov.toodoo.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.yaroslavgamayunov.toodoo.di.ApplicationContext
import javax.inject.Inject

interface PreferenceHelper {
    fun <T> getPreference(preferenceKey: String, defaultValue: T): T
    fun <T> putPreference(preferenceKey: String, value: T)
}

class DefaultPreferenceHelper @Inject constructor(
    @ApplicationContext
    context: Context,
) : PreferenceHelper {
    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPreference(
        preferenceKey: String,
        defaultValue: T,
    ): T {
        return when (defaultValue) {
            is Long -> preferences.getLong(preferenceKey, defaultValue)
            else -> null
        } as? T ?: throw TypeNotSupportedByPreferenceHelperException(defaultValue)
    }

    override fun <T> putPreference(preferenceKey: String, value: T) {
        preferences.edit {
            when (value) {
                is Long -> putLong(preferenceKey, value)
                else -> throw TypeNotSupportedByPreferenceHelperException(value)
            }
        }
    }

    private companion object {
        const val SHARED_PREFERENCES_NAME = "application_preferences"
    }
}

class TypeNotSupportedByPreferenceHelperException(value: Any?) :
    RuntimeException(
        "Preference helper does not have support for ${value?.let { it::class.java.name }}"
    )