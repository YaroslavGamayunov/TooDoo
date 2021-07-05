package com.yaroslavgamayunov.toodoo.work

import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkerFactory
import javax.inject.Inject

class TooDooWorkerFactory @Inject constructor(
    factories: MutableSet<WorkerFactory>
) : DelegatingWorkerFactory() {
    init {
        factories.forEach(::addFactory)
    }
}