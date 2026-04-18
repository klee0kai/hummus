package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <T> singleEventFlow(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
) = channelFlow {
    val result = block()
    send(result)
}.flowOn(coroutineContext)

suspend fun Flow<Unit>.onTicks(block: suspend () -> Unit) {
    merge(this, flowOf(Unit)).collect {
        block()
    }
}

suspend fun <Arg> Flow<Arg>.onTicks(init: Arg, block: suspend (arg: Arg) -> Unit) {
    merge(flowOf(init), this).collectLatest {
        block(it)
    }
}

inline fun <reified T> Flow<T>.changeFilter(
    crossinline filter: suspend (old: T?, new: T) -> Boolean
): Flow<T> = runningFold(arrayOf()) { accumulator: Array<T>, value: T ->
    if (accumulator.isEmpty()) {
        arrayOf(value)
    } else {
        arrayOf(accumulator.last(), value)
    }
}.filter { array ->
    when (array.size) {
        0 -> false
        1 -> filter.invoke(null, array.last())
        else -> filter.invoke(array.first(), array.last())
    }
}.map { it.last() }


suspend inline fun <reified T> Flow<T>.await(timeout: Duration): T? =
    withTimeout(timeout) { firstOrNull() }

suspend inline fun <reified T> Flow<T>.awaitSec(): T? = await(1.seconds)


/**
 * Collects values from a Flow and sends them to a ProducerScope channel.
 *
 * Bridges a [Flow] to a [ProducerScope] by launching a coroutine that collects
 * from the flow and sends each value to the producer's channel. Useful for
 * converting flows into channels or producer blocks.
 *
 * **Behavior:**
 * - Launches a new coroutine in the producer's scope
 * - Collects values from this flow
 * - Sends each value through [ProducerScope.channel]
 * - Completes when flow ends or scope is cancelled
 *
 * **Usage:**
 * ```kotlin
 * val flow = (1..5).asFlow()
 *
 * val channel = produce {
 *     flow.collectTo(this)  // Send all flow values to channel
 * }
 *
 * channel.consume {
 *     for (value in this) {
 *         println(value)  // 1, 2, 3, 4, 5
 *     }
 * }
 * ```
 *
 * **Error handling:**
 * If the flow throws, the exception propagates through the channel and closes it.
 *
 * @param T the type of values in the flow and channel
 * @param consumer the [ProducerScope] that receives the values
 */
fun <T> Flow<T>.collectTo(consumer: ProducerScope<T>) {
    consumer.launch {
        collect { consumer.channel.send(it) }
    }
}

/**
 * Buffers the last N values from a flow.
 *
 * Transforms a [Flow<T>] into a [Flow<List<T>>] where each emitted list contains
 * up to the last [maxSize] values seen. As new values arrive, old ones are dropped
 * from the beginning of the buffer.
 *
 * **Behavior:**
 * - Emits empty list when flow starts
 * - Emits list with 1 item after first value
 * - Emits list with min(N, maxSize) items as buffer fills
 * - Maintains sliding window of last [maxSize] items
 * - Resets only when flow completes
 *
 * **Usage:**
 * ```kotlin
 * val flow = (1..10).asFlow()
 *
 * flow.bufferLast(3).collect { buffer ->
 *     println(buffer)
 * }
 *
 * // Output:
 * // []
 * // [1]
 * // [1, 2]
 * // [1, 2, 3]
 * // [2, 3, 4]
 * // [3, 4, 5]
 * // ... [8, 9, 10]
 * ```
 *
 * **Use cases:**
 * - Recent N items (e.g., "last 5 messages")
 * - Moving average computation
 * - Buffering for batch processing
 * - Maintaining recent state history
 *
 * **Memory:**
 * Keeps at most [maxSize] items in memory at any time.
 *
 * @param T the type of values in the flow
 * @param maxSize maximum number of items to keep (must be > 0)
 * @return new flow emitting lists of buffered items
 *
 * @see scan for understanding the underlying mechanism
 */
inline fun <reified T> Flow<T>.bufferLast(
    maxSize: Int
): Flow<List<T>> = scan(emptyList()) { acc, value ->
    (acc + value).takeLast(maxSize)
}