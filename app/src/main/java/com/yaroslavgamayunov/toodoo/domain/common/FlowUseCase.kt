package com.yaroslavgamayunov.toodoo.domain.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(params: P): Flow<Result<R>> {
        return execute(params)
            .catch { error -> emit(Result.Error(Exception(error))) }
            .flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(params: P): Flow<Result<R>>
}