package com.github.klee0kai.hummus.threads

import com.github.klee0kai.hummus.Hummus
import com.github.klee0kai.hummus.Hummus.hummusLog
import java.util.concurrent.*

object Threads {

    var defUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    fun newSingleThreadExecutor(poolName: String?): ThreadPoolExecutor {
        return object : ThreadPoolExecutor(
            0, 1, 0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            DefaultThreadFactory(poolName!!, false),
            if (Hummus.isDebug) AbortPolicy() else DiscardPolicy()
        ) {
            override fun afterExecute(r: Runnable, t: Throwable?) {
                var t: Throwable? = t
                super.afterExecute(r, t)
                if (t == null && r is Future<*>) {
                    try {
                        val future = r as Future<*>
                        if (future.isDone) {
                            future.get()
                        }
                    } catch (ee: CancellationException) {
                        t = ee
                    } catch (ee: ExecutionException) {
                        t = ee
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                if (t != null) {
                    defUncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), t)
                }
            }
        }
    }

    fun newSingleThreadExecutor(poolName: String?, daemon: Boolean): ThreadPoolExecutor {
        return object : ThreadPoolExecutor(
            0, 1, 0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            DefaultThreadFactory(poolName!!, daemon),
            if (Hummus.isDebug) AbortPolicy() else DiscardPolicy()
        ) {
            override fun afterExecute(r: Runnable, t: Throwable?) {
                var t: Throwable? = t
                super.afterExecute(r, t)
                if (t == null && r is Future<*>) {
                    try {
                        val future = r as Future<*>
                        if (future.isDone) {
                            future.get()
                        }
                    } catch (ee: CancellationException) {
                        t = ee
                    } catch (ee: ExecutionException) {
                        t = ee
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                if (t != null) {
                    defUncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), t)
                } else {
                    hummusLog?.w(t = t)
                }
            }
        }
    }

    fun newCachedPoolThreadExecutor(poolName: String?): ThreadPoolExecutor {
        return object : ThreadPoolExecutor(
            0, Int.MAX_VALUE, 0L,
            TimeUnit.MILLISECONDS,
            SynchronousQueue(),
            DefaultThreadFactory(poolName!!, false),
            if (Hummus.isDebug) AbortPolicy() else DiscardPolicy()
        ) {
            override fun afterExecute(r: Runnable, t: Throwable?) {
                var t: Throwable? = t
                super.afterExecute(r, t)
                if (t == null && r is Future<*>) {
                    try {
                        val future = r as Future<*>
                        if (future.isDone) {
                            future.get()
                        }
                    } catch (ee: CancellationException) {
                        t = ee
                    } catch (ee: ExecutionException) {
                        t = ee
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                if (t != null) {
                    defUncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), t)
                } else {
                    hummusLog?.w(t = t)
                }
            }
        }
    }

    fun startThread(r: Runnable?) {
        Thread(r).start()
    }

    fun trySleep(millis: Long) {
        if (millis <= 0) return
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    fun <T> tryGet(future: Future<T>): T? {
        try {
            return future.get()
        } catch (ignore: ExecutionException) {
            //ignore
        } catch (ignore: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return null
    }
}