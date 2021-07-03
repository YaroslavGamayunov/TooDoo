package com.yaroslavgamayunov.toodoo.domain.common

import java.lang.Exception

sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val e: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

inline fun <T> Result<T>.doIfSuccess(block: (T) -> Unit) {
    if (this is Result.Success) {
        block(this.data)
    }
}