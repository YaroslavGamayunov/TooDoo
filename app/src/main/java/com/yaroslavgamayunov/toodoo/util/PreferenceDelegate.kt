package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceDelegate<T>(
    val context: Context,
    private val preferenceKey: String,
    private val defaultValue: T,
) : ReadWriteProperty<Any?, T> {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (defaultValue) {
            is Long -> preferences.getLong(preferenceKey, defaultValue)
            else -> null
        } as? T ?: throw TypeNotSupportedByPreferencesDelegateException(defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        preferences.edit {
            when (value) {
                is Long -> putLong(preferenceKey, value)
                else -> throw TypeNotSupportedByPreferencesDelegateException(value)
            }
        }
    }

    private companion object {
        const val SHARED_PREFERENCES_NAME = "application_preferences"
    }
}

class TypeNotSupportedByPreferencesDelegateException(value: Any?) :
    RuntimeException(
        "Preferences delegate does not have support for ${value?.let { it::class.java.name }}"
    )