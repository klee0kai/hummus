package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Creates a child coroutine scope with independent job supervision.
 *
 * Creates a new [CoroutineScope] that inherits the context from this scope but uses a
 * new [SupervisorJob] as a child of this scope's job. This allows the child scope to
 * handle failures independently without cancelling other children of the parent.
 *
 * **Job hierarchy:**
 * - Parent scope's job is the parent
 * - New SupervisorJob becomes the child job
 * - Children of the new scope are supervised by the SupervisorJob
 * - If one child fails, others continue (supervisor semantics)
 *
 * **Use cases:**
 * - Creating independent task groups (one failure doesn't cancel siblings)
 * - Managing sub-scopes within a larger scope
 * - Preventing cascading cancellations
 *
 * **vs. Regular Job:**
 * - Regular Job: one child failure cancels all siblings
 * - SupervisorJob: one child failure doesn't affect siblings
 *
 * **Usage example:**
 * ```kotlin
 * val parentScope = CoroutineScope(Job() + Dispatchers.Main)
 * val childScope = parentScope.childSupervisedScope()
 *
 * // Launch independent tasks in child scope
 * childScope.launch {
 *     try {
 *         failingTask()  // This fails
 *     } catch (e: Exception) {
 *         // Handle failure
 *     }
 * }
 *
 * childScope.launch {
 *     // This continues running even if previous task failed
 *     continuingTask()
 * }
 * ```
 *
 * **Cancellation:**
 * When parent scope is cancelled, child scope is also cancelled (due to job hierarchy).
 * But failures in one child job don't affect others.
 *
 * @return new [CoroutineScope] with a [SupervisorJob] connected to this scope's job
 *
 * @see SupervisorJob
 * @see CoroutineScope
 */
fun CoroutineScope.childSupervisedScope(
) = CoroutineScope(coroutineContext + SupervisorJob(coroutineContext[Job]))

/**
 * Launches an async coroutine with exception wrapping and optional tracking.
 *
 * Executes [block] asynchronously and wraps the result (success or failure) in [Result].
 * Never throws on [Deferred.await]; instead returns success or failure through [Result].
 * Optionally tracks execution via [trackFlow] counter.
 *
 * **Exception handling:**
 * - Exceptions are caught and wrapped in [Result.failure]
 * - [await] never throws; use [Result.getOrNull] or [Result.exceptionOrNull]
 * - Allows graceful error handling without try-catch on [await]
 *
 * **Tracking:**
 * If [trackFlow] is provided:
 * - Incremented when block starts
 * - Decremented when block completes (even on exception)
 * - Useful for counting active async operations
 *
 * **Usage:**
 * ```kotlin
 * val scope = CoroutineScope(Dispatchers.IO)
 * val activeCount = MutableStateFlow(0)
 *
 * val resultJob = scope.asyncCaching(trackFlow = activeCount) {
 *     api.fetchData()  // May throw
 * }
 *
 * val result = resultJob.await()
 * when {
 *     result.isSuccess -> println("Data: ${result.getOrNull()}")
 *     result.isFailure -> println("Error: ${result.exceptionOrNull()}")
 * }
 * ```
 *
 * **vs. [asyncTracked]:**
 * - [asyncCaching]: wraps exceptions in Result
 * - [asyncTracked]: throws exceptions on await
 *
 * @param R the return type of the block
 * @param trackFlow optional counter to track active operations
 * @param block suspend function to execute
 * @return [Deferred<Result<R>>] with result or failure wrapped
 *
 * @see asyncTracked for throwing exceptions
 */
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


/**
 * Launches an async coroutine with optional activity tracking.
 *
 * Executes [block] asynchronously and returns a [Deferred<R>] with the result.
 * If block throws, exception is re-thrown on [await]. Optionally tracks active
 * operations via [trackFlow] counter.
 *
 * **Exception handling:**
 * - Exceptions are thrown through [Deferred.await]
 * - Wrap in try-catch if you need graceful error handling
 *
 * **Tracking:**
 * If [trackFlow] is provided:
 * - Incremented when block starts
 * - Decremented when block completes (in finally, even on exception)
 * - Useful for counting in-flight async operations
 *
 * **Usage:**
 * ```kotlin
 * val scope = CoroutineScope(Dispatchers.IO)
 * val inFlightCount = MutableStateFlow(0)
 *
 * val resultJob = scope.asyncTracked(trackFlow = inFlightCount) {
 *     database.queryData()
 * }
 *
 * try {
 *     val result = resultJob.await()
 *     println("Result: $result")
 * } catch (e: Exception) {
 *     println("Failed: ${e.message}")
 * }
 * ```
 *
 * **vs. [asyncCaching]:**
 * - [asyncTracked]: throws exceptions on await
 * - [asyncCaching]: wraps exceptions in Result
 *
 * @param R the return type of the block
 * @param trackFlow optional counter to track active operations
 * @param block suspend function to execute and return result
 * @return [Deferred<R>] providing the result when awaited
 *
 * @see asyncCaching for Result-wrapped exceptions
 */
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

/**
 * Launches a fire-and-forget coroutine with optional activity tracking.
 *
 * Like [launch], but optionally increments/decrements [trackFlow] counter
 * to track how many jobs are currently running.
 *
 * **Tracking behavior:**
 * - On start: [trackFlow] is incremented
 * - On end: [trackFlow] is decremented (in finally, even on exception)
 * - Useful for knowing how many background jobs are active
 *
 * **Exception handling:**
 * - Exceptions thrown in [block] are not caught
 * - Use coroutine exception handlers to handle uncaught exceptions
 * - Counter is decremented even if block throws
 *
 * **Usage:**
 * ```kotlin
 * val scope = CoroutineScope(Dispatchers.Main)
 * val jobCount = MutableStateFlow(0)
 *
 * scope.launchTracked(trackFlow = jobCount) {
 *     delay(1000)
 *     updateUI()
 * }
 *
 * // jobCount increments, decrements when job finishes
 * ```
 *
 * **Typical use case:**
 * ```kotlin
 * val scope = CoroutineScope(Job() + Dispatchers.Main)
 * val isLoading = MutableStateFlow(false)
 * val activeJobs = MutableStateFlow(0)
 *
 * scope.launchTracked(trackFlow = activeJobs) {
 *     isLoading.value = activeJobs.value > 0
 *     fetchData()
 * }
 * ```
 *
 * @param trackFlow optional counter incremented on start, decremented on end
 * @param block suspend function to execute
 * @return [Job] that can be cancelled or awaited
 *
 * @see asyncTracked for async version
 */
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

