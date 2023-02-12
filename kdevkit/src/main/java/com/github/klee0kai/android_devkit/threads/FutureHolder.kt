package com.github.klee0kai.android_devkit.threads

import com.github.klee0kai.android_devkit.AndroidDevKitLogs.devKitLog
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

open class FutureHolder<T> {

    private var future: Future<T>? = null

    val isInProcess: Boolean
        get() = future?.isDone != true && future?.isCancelled != true


    fun set(future: Future<T>): Future<T> {
        return future.also { this.future = it }
    }

    fun get(): Future<T>? {
        return future
    }

    fun cancel() {
        if (future?.isDone != true) {
            future?.cancel(false)
        }
    }

    fun tryAwait(): T? = runCatching {
        future?.get()
    }.onFailure {
        devKitLog?.w(it)
    }.getOrNull()

    fun result(): T? = runCatching {
        if (future?.isDone == true) {
            future?.get(1, TimeUnit.MICROSECONDS)
        } else {
            null
        }
    }.onFailure {
        devKitLog?.w(it)
    }.getOrNull()


    fun popResult(): T? {
        return result()?.also {
            future = null
        }
    }
}