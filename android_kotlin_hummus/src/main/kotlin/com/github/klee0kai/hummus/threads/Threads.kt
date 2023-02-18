package com.github.klee0kai.hummus.threads

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper


fun Threads.newThreadLooper(nameThread: String?): Looper {
    val handlerThread = HandlerThread(nameThread)
    handlerThread.isDaemon = true
    handlerThread.start()
    return handlerThread.looper
}

fun Threads.runMain(runnable: Runnable?) {
    Handler(Looper.getMainLooper()).post(runnable!!)
}

fun Threads.runMainDelayed(delay: Long, runnable: Runnable?): Handler {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed(runnable!!, delay)
    return handler
}

