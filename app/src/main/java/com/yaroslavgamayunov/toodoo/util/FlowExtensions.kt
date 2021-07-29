package com.yaroslavgamayunov.toodoo.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.domain.common.map
import com.yaroslavgamayunov.toodoo.exception.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list -> list.map { transform(it) } }

/**
 * Catches all errors and wraps them in [Result.Error] and maps other values to [Result.Success]
 */
inline fun <T> Flow<T>.toResultFlow(
    crossinline failureResolver: (Exception) -> Failure = { e -> Failure.Unknown(e) },
): Flow<Result<T>> = this
    .map<T, Result<T>> { Result.Success(it) }
    .catch { emit(Result.Error(failureResolver.invoke(java.lang.Exception(it)))) }

inline fun <T, R> Flow<Result<T>>.mapResult(
    crossinline transform: (T) -> R,
): Flow<Result<R>> = this.map { result -> result.map { transform(it) } }

inline fun <T> Flow<T>.collectIn(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            collect(action)
        }
    }
}