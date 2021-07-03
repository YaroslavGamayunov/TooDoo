package com.yaroslavgamayunov.toodoo.util

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.notifyObservers() {
    this.value = this.value
}