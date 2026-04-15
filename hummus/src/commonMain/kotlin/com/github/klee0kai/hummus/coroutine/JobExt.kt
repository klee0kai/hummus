@file:OptIn(ExperimentalTime::class)

package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.*
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Creates an already-completed empty Job.
 *
 * Useful for APIs that require a [Job] but you have no actual work to do.
 * The job is immediately in completed state and can be awaited without blocking.
 *
 * **Use cases:**
 * - Default/fallback jobs in conditional logic
 * - Joining with multiple jobs where some are optional
 * - Testing scenarios where you need a completed job
 * - Placeholder in job aggregation
 *
 * **Example:**
 * ```kotlin
 * val job = if (shouldDoWork) {
 *     scope.launch { doSomeWork() }
 * } else {
 *     emptyJob()  // Completed immediately
 * }
 *
 * job.join()  // Returns immediately if it was emptyJob()
 * ```
 *
 * **State:**
 * - isActive: false (already completed)
 * - isCompleted: true
 * - isCancelled: false
 *
 * @return completed Job with no work
 *
 * @see Job
 * @see completeAsync for a deferred version
 */
fun emptyJob(): Job = Job().also { it.complete() }

/**
 * Creates an already-resolved Deferred with the given result.
 *
 * Similar to [emptyJob] but for [Deferred]. The deferred is immediately completed
 * with the provided value and can be awaited without any delay.
 *
 * **Use cases:**
 * - Returning cached/immediate results without launching coroutines
 * - Conditional async operations with fallback immediate values
 * - Testing async code with predefined results
 * - Wrapping synchronous results in async API
 *
 * **Example:**
 * ```kotlin
 * suspend fun fetchData(id: String): String {
 *     return if (id in cache) {
 *         // Return cached result without suspending
 *         completeAsync(cache[id]!!).await()
 *     } else {
 *         // Fetch from network
 *         loadFromNetwork(id)
 *     }
 * }
 * ```
 *
 * **vs. emptyJob():**
 * - [emptyJob]: returns nothing (Unit), completes job
 * - [completeAsync]: returns a value of type T, completes deferred
 *
 * **Execution:**
 * - Does not launch any coroutine
 * - Returns immediately
 * - [await] on result returns immediately with the value
 *
 * @param T type of the result value
 * @param result the value to return when awaited
 * @return Deferred that is already resolved with [result]
 *
 * @see emptyJob for job version
 * @see CompletableDeferred
 */
fun <T> completeAsync(result: T): Deferred<T> = CompletableDeferred(value = result)

/**
 * Executes a suspend block and ensures it takes at least the specified duration.
 *
 * Runs the block and measures its execution time. If it completes faster than [duration],
 * adds a delay to ensure the total time is at least [duration]. Useful for animations,
 * loading screens, or ensuring operations don't complete too quickly.
 *
 * **Execution timeline:**
 * ```
 * Start -> [Block runs: 200ms] -> [Delay: 800ms] -> End (1000ms minimum)
 * ```
 *
 * **Use cases:**
 * - Loading screens that should show for minimum time (UX)
 * - Animations that need consistent duration
 * - Testing with artificial delays
 * - Preventing flashing UI elements
 *
 * **Example:**
 * ```kotlin
 * // Ensure loading screen shows for at least 1 second
 * minDuration(1.seconds) {
 *     loadData()  // Might complete in 100ms
 * }
 * // Total execution: 1 second (100ms work + 900ms delay)
 * ```
 *
 * **Performance notes:**
 * - Blocks are executed sequentially (not in parallel)
 * - Uses system clock for timing (not thread local)
 * - Additional delay is >= 0 (no "speed up")
 *
 * **Exception handling:**
 * If [block] throws, exception is re-thrown immediately without minimum delay.
 *
 * @param T return type of the block
 * @param duration minimum total duration for execution
 * @param block suspend function to execute
 * @return the result from block
 *
 * @see kotlinx.coroutines.delay
 * @see awaitSec for timeout version
 */
suspend fun <T> minDuration(duration: Duration, block: suspend () -> T): T {
    val start = Clock.System.now().toEpochMilliseconds()
    val r = block.invoke()
    val left = duration - (Clock.System.now().toEpochMilliseconds() - start).milliseconds
    if (left.isPositive()) delay(left)
    return r
}

/**
 * Awaits a Deferred with a 1-second timeout.
 *
 * Convenience function that waits for the deferred to complete, but cancels
 * if it takes longer than 1 second. Returns null if timeout occurs instead of
 * throwing an exception.
 *
 * **Behavior:**
 * - Waits up to 1 second for result
 * - Returns null if timeout expires (no exception)
 * - Returns result immediately if available
 *
 * **Use cases:**
 * - Short operations with fail-fast requirement
 * - Network requests with reasonable timeout
 * - Testing with timeout safety
 * - Avoiding indefinite hangs
 *
 * **Example:**
 * ```kotlin
 * val result = async {
 *     fetchDataWithRetry()
 * }
 *
 * when (val data = result.awaitSec()) {
 *     null -> showTimeoutError()  // Took > 1 second
 *     else -> showData(data)      // Got result
 * }
 * ```
 *
 * **vs. await():**
 * - [await]: blocks indefinitely until result
 * - [awaitSec]: times out after 1 second, returns null
 *
 * **vs. withTimeout:**
 * - [awaitSec]: returns null on timeout
 * - [withTimeout]: throws TimeoutCancellationException on timeout
 *
 * **Note:**
 * If the deferred is already completed (synchronously), returns result immediately
 * without any delay.
 *
 * @param T the type of the deferred result
 * @return the result if completed within 1 second, or null if timeout
 *
 * @see withTimeoutOrNull
 * @see minDuration for minimum duration requirement
 */
suspend inline fun <reified T> Deferred<T>.awaitSec(
): T? = withTimeoutOrNull(1.seconds) {
    await()
}
