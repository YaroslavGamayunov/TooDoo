package com.yaroslavgamayunov.toodoo.exception

import retrofit2.HttpException
import java.lang.Exception

fun resolveNetworkFailure(exception: Exception): Failure {
    if (exception is HttpException) {
        if (exception.code() == 500) {
            return Failure.ServerError
        }
    }
    return Failure.NetworkConnection
}