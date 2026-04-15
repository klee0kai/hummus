package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun CoroutineScope.childSupervisedScope(
) = CoroutineScope(coroutineContext + SupervisorJob(coroutineContext[Job]))


fun <T> Flow<T>.collectTo(consumer: ProducerScope<T>) {
    consumer.launch {
        collect { consumer.channel.send(it) }
    }
}

fun <R> CoroutineScope.asyncCaching(
    trackFlow: MutableStateFlow<Int>? = null,
    block: suspend () -> R,
): Deferred<Result<R>> = async {
    trackFlow?.update { it + 1 }
    val result = runCatching {
        block()
    }
    trackFlow?.update { it - 1 }
    result
}


fun <R> CoroutineScope.asyncTracked(
    trackFlow: MutableStateFlow<Int>? = null,
    block: suspend () -> R,
): Deferred<R> = async {
    trackFlow?.update { it + 1 }
    val result = try {
        block()
    } finally {
        trackFlow?.update { it - 1 }
    }

    result
}

fun CoroutineScope.launchTracked(
    trackFlow: MutableStateFlow<Int>? = null,
    block: suspend () -> Unit,
): Job = launch {
    trackFlow?.update { it + 1 }
    try {
        block()
    } finally {
        trackFlow?.update { it - 1 }
    }
}

inline fun <reified T> Flow<T>.bufferLast(
    maxSize: Int
): Flow<List<T>> = scan(emptyList()) { acc, value ->
    (acc + value).takeLast(maxSize)
}