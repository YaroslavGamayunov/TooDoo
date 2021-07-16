package com.yaroslavgamayunov.toodoo.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<List<T>>.mapList(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list -> list.map { transform(it) } }
