package com.github.klee0kai.hummus.threads

import com.github.klee0kai.hummus.threads.Threads
import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor

class ThreadTests {

    private val RUN_COUNT = 300

    @Test
    @Throws(InterruptedException::class, ExecutionException::class)
    fun sequence_run() {
        //Give
        val runs = LinkedList<Int>()
        val secThread: ThreadPoolExecutor = Threads.newSingleThreadExecutor("secThread")

        //When
        var lastFuture: Future<*>? = null
        for (i in 0 until RUN_COUNT) {
            lastFuture = secThread.submit { runs.add(i) }
        }
        lastFuture!!.get()

        //then
        for (i in 0 until RUN_COUNT) {
            Assert.assertEquals(i.toLong(), runs[i].toLong())
        }
    }
}