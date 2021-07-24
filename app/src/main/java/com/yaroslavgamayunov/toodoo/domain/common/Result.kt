package com.yaroslavgamayunov.toodoo.domain.common

import com.yaroslavgamayunov.toodoo.exception.Failure

sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val failure: Failure) : Result<Nothing>()

    companion object
}

inline fun <T> Result<T>.fold(onSuccess: (T) -> Unit, onError: (Failure) -> Unit) {
    when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(failure)
    }
}

inline fun <T> Result<T>.doIfSuccess(action: (T) -> Unit): Result<T> =
    this.apply {
        if (this is Result.Success) {
            action(this.data)
        }
    }

inline fun <T> Result<T>.doIfError(action: (Failure) -> Unit): Result<T> =
    this.apply {
        if (this is Result.Error) {
            action(this.failure)
        }
    }

inline fun <T> Result.Companion.catch(
    failureResolver: (Exception) -> Failure = { e -> Failure.Unknown(e) },
    f: () -> T,
): Result<T> {
    return try {
        Result.Success(f())
    } catch (e: Exception) {
        Result.Error(failureResolver(e))
    }
}

infix fun <T, U> Result<T>.map(transform: (T) -> U): Result<U> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(failure)
}

suspend infix fun <T, U> Result<T>.then(f: suspend (T) -> Result<U>) =
    when (this) {
        is Result.Success -> f(data)
        is Result.Error -> Result.Error(failure)
    }


suspend infix fun <T, U> T.teePipeTo(f: suspend (T) -> Result<U>): Result<T> =
    Result.Success(this).tee(f)

suspend infix fun <T, U> T.pipeTo(f: suspend (T) -> Result<U>): Result<U> =
    Result.Success(this).then(f)

/**
 * This functional operator is analogue of R's language %T>% operator,
 * In short, it takes [Result] as a parameter of [f], then evaluates [f]
 * and returns the same [Result] used in input or [Result.Error]
 * if [f] returned it
 * About the name of this operator: 'tee' is named after a T-shaped pipe.
 *
 *
 * A simple illustration of this operator:
 * ```
 * ------------------------------------
 *  x: Result<T>   ----> ----> ----> x (returns same x that was passed as a parameter of f)
 * -------------|   |   |--------------
 *              |   |   |
 *              |   V   |
 *              |  f(x) |
 *              |       |
 * ```
 */
suspend infix fun <T, U> Result<T>.tee(f: suspend (T) -> Result<U>): Result<T> =
    this.then(f).then { this }

/**
 * It is a function convenient to convert Result<T> to Result<Unit>
 * (when data of successful result is not important)
 *
 * Example:
 * ```
 *
 * val res: Result<Unit> = Result.Success<Int>(10) then toResultUnit()
 *
 * ```
 */
fun <T>  toResultUnit(): suspend (T) -> Result.Success<Unit> = {
    Result.Success(Unit)
}