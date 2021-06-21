package com.yaroslavgamayunov.toodoo.domain.common

import java.lang.Exception

sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val e: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}