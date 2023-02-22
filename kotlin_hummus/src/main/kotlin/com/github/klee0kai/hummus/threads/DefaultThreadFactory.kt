package com.github.klee0kai.hummus.threads

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

open class DefaultThreadFactory(
    poolname: String,
    private val isDaemonThreads: Boolean
) : ThreadFactory {

    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup as ThreadGroup
        namePrefix = poolname + "-" + poolNumber.getAndIncrement() + "-thread-"
    }

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0)
        if (t.isDaemon) t.isDaemon = isDaemonThreads
        if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
        return t
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }
}