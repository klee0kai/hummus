package com.github.klee0kai.hummus.coroutine

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

interface LazyStateFlow<T, in Arg> : MutableStateFlow<T>, TouchableFlow<T, Arg> {

    /**
     * cancel current update
     */
    fun cancelTouch()

}

fun <T, Arg> lazyStateFlow(
    /**
     * init state value
     */
    init: T,
    /**
     * The default value of the argument when first used,
     * and also after being used on touch
     */
    defaultArg: Arg,
    /**
     * scope to run update method
     */
    scope: CoroutineScope,
    /**
     * called only once on first subscriber
     * or on touch
     */
    block: suspend MutableStateFlow<T>.(arg: Arg) -> Unit,
): LazyStateFlow<T, Arg> {
    val lastCanceled = atomic(false)
    val consumers = atomic(0)
    val job = atomic<Job?>(null)
    var restartArg = defaultArg
    val stateFlowMirror = MutableStateFlow(init)

    val restart: () -> Job = {
        val newJob = scope.launch {
            if (consumers.value <= 0) {
                // no consumers, update not need
                return@launch
            }
            val curArg = restartArg
            restartArg = defaultArg // argument is used. reset to default
            block(stateFlowMirror, curArg)
        }
        job.getAndSet(newJob)?.cancel()
        newJob
    }

    val channelFlow = channelFlow<T> {
        if (consumers.getAndIncrement() == 0 || lastCanceled.getAndSet(false)) restart()
        stateFlowMirror.collectTo(this)
        awaitClose {
            if (consumers.decrementAndGet() == 0) {
                job.getAndSet(null)?.cancel()
            }
        }
    }

    return object : LazyStateFlow<T, Arg>, MutableStateFlow<T> by stateFlowMirror {

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            channelFlow.collect(collector)
            error("collect should not end")
        }

        override fun touch(arg: Arg) {
            restartArg = arg
            restart()
        }

        override fun cancelTouch() {
            lastCanceled.value = true
            job.updateAndGet { last ->
                last?.cancel()
                null
            }
        }

    }

}