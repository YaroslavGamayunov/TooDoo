package com.yaroslavgamayunov.toodoo.domain.common

import com.yaroslavgamayunov.toodoo.exception.Failure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

abstract class FlowUseCase<in P, out R>(val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(params: P): Flow<Result<R>> {
        return execute(params)
            .catch { e -> emit(Result.Error(Failure.Unknown(Exception(e)))) }
            .flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(params: P): Flow<Result<R>>
}