package com.yaroslavgamayunov.toodoo.util

import com.yaroslavgamayunov.toodoo.data.PreferenceHelper
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceDelegate<T>(
    private val preferenceHelper: PreferenceHelper,
    private val preferenceKey: String,
    private val defaultValue: T,
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return preferenceHelper.getPreference(preferenceKey, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        preferenceHelper.putPreference(preferenceKey, value)
    }
}