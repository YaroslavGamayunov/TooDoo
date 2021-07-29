package com.yaroslavgamayunov.toodoo.domain.common

import com.yaroslavgamayunov.toodoo.exception.Failure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class UseCase<in P, out R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(params)
            }
        } catch (e: Exception) {
            Timber.d(e.cause)
            Result.Error(Failure.Unknown(e))
        }
    }

    protected abstract suspend fun execute(params: P): Result<R>
}