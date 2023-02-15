package com.github.klee0kai.android_devkit.threads

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.github.klee0kai.android_devkit.AndroidDevKitLogs.devKitLog
import com.github.klee0kai.android_devkit.BuildConfig
import java.util.concurrent.*

object Threads {

    var defUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    fun newThreadLooper(nameThread: String?): Looper {
        val handlerThread = HandlerThread(nameThread)
        handlerThread.isDaemon = true
        handlerThread.start()
        return handlerThread.looper
    }

    fun newSingleThreadExecutor(poolName: String?): ThreadPoolExecutor {
        return object : ThreadPoolExecutor(
            0, 1, 0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            DefaultThreadFactory(poolName!!, false),
            if (BuildConfig.DEBUG) AbortPolicy() else DiscardPolicy()
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
            if (BuildConfig.DEBUG) AbortPolicy() else DiscardPolicy()
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
                    devKitLog?.w(t = t)
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
            if (BuildConfig.DEBUG) AbortPolicy() else DiscardPolicy()
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
                    devKitLog?.w(t = t)
                }
            }
        }
    }

    fun startThread(r: Runnable?) {
        Thread(r).start()
    }

    fun runMain(runnable: Runnable?) {
        Handler(Looper.getMainLooper()).post(runnable!!)
    }

    fun runMainDelayed(delay: Long, runnable: Runnable?): Handler {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable!!, delay)
        return handler
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