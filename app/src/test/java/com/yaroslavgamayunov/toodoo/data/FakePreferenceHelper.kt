package com.yaroslavgamayunov.toodoo.data

class FakePreferenceHelper : PreferenceHelper {
    private val preferences: MutableMap<String, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPreference(preferenceKey: String, defaultValue: T): T {
        return preferences[preferenceKey]?.let { it as? T } ?: defaultValue
    }

    override fun <T> putPreference(preferenceKey: String, value: T) {
        preferences[preferenceKey] = value as Any
    }
}