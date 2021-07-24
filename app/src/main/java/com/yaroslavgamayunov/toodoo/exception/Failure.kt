package com.yaroslavgamayunov.toodoo.exception

sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object SynchronizationError : Failure()

    data class Unknown(val e: Exception) : Failure()
}