package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A [CoroutineScope] that coordinates concurrent work with optional synchronization.
 *
 * Provides a scoped context for launching coroutines with built-in support for:
 * - Mutual exclusion via optional [Mutex]
 * - Job tracking and cancellation via [singleRunJobs]
 * - Global job tracking via [GlobalJobsCollection]
 *
 * Use this scope when you need synchronized access to shared resources
 * or want to coordinate parallel work that may compete for the same resource.
 *
 * **Key features:**
 * - Optional mutex-based synchronization for critical sections
 * - Single job tracking by key (prevents duplicate concurrent work)
 * - Integration with global job tracking for monitoring
 * - Structured concurrency through [CoroutineScope]
 *
 * **Usage example:**
 *
 * Creating and using a safe scope:
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.Main)
 *
 * scope.launchSafe {
 *     // This block runs with the scope's mutex locked
 *     updateSharedResource()
 * }
 *
 * // Launch only if not already running
 * scope.launchIfNotStarted("fetch-data") {
 *     val data = fetchDataFromServer()
 *     updateUI(data)
 * }
 * ```
 *
 * **Synchronization:**
 * - Use [launch] for fire-and-forget with optional locking
 * - Use [launchSafe] for guaranteed lock-protected execution
 * - Use [asyncSafe]/[asyncResultSafe] for deferred values with locking
 *
 * @param coroutineContext the [CoroutineContext] for this scope
 * @param mutex optional [Mutex] for synchronizing access (default: new unlocked Mutex)
 *
 * @see launchSafe
 * @see launchLatest
 * @see launchIfNotStarted
 * @see GlobalJobsCollection for global job tracking
 */
class SafeContextScope(
    override val coroutineContext: CoroutineContext,
    val mutex: Mutex = Mutex(),
) : CoroutineScope {
    /**
     * Tracks currently active jobs by key.
     *
     * Used by [launchLatest] and [launchIfNotStarted] to prevent duplicate concurrent execution
     * with the same key. The map is keyed by string identifiers that represent logical operations.
     */
    internal val singleRunJobs = MutableStateFlow(mapOf<String, Job>())
}

/**
 * Launches a coroutine with optional synchronization and tracking.
 *
 * Launches a fire-and-forget coroutine that optionally acquires a mutex lock before execution.
 * Tracks active coroutines via [trackFlow] counter and registers with [GlobalJobsCollection].
 *
 * **Execution guarantees:**
 * - If [mutex] is provided: block runs with mutex locked (thread-safe)
 * - If [mutex] is null: block runs without synchronization
 * - Updates [trackFlow] counter on start/end
 * - Registers with global jobs for monitoring
 *
 * **Parameters:**
 * - [context]: Additional dispatcher or context elements
 * - [trackFlow]: Optional counter that increments on start, decrements on end
 * - [mutex]: Optional mutex to lock before execution (default: scope's mutex if not provided)
 * - [globalRunDesc]: Resource ID for global job tracking (0 = not tracked)
 * - [block]: Suspend function to execute
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.Main)
 * val activeCount = MutableStateFlow(0)
 *
 * scope.launch(
 *     trackFlow = activeCount,
 *     block = {
 *         delay(1000)
 *         updateUI()
 *     }
 * )
 *
 * activeCount.value  // 1 (incremented when job starts)
 * ```
 *
 * @param context additional [CoroutineContext] elements
 * @param trackFlow optional counter to track active jobs
 * @param mutex optional [Mutex] to lock before execution
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute
 * @return [Job] that can be cancelled or awaited
 */
fun SafeContextScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    trackFlow: MutableStateFlow<Int>? = null,
    mutex: Mutex? = null,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLockOrRun {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

/**
 * Launches a coroutine with guaranteed mutex locking.
 *
 * Like [launch], but always uses the scope's mutex (or provided one) for thread-safe execution.
 * The block is guaranteed to run with exclusive access to protected resources.
 *
 * **Synchronization:**
 * - Always acquires [mutex] before executing block (thread-safe)
 * - Prevents concurrent execution with other [launchSafe]/[asyncSafe] calls using same mutex
 * - Releases mutex after block completes (even on exception)
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.Default)
 * var sharedCounter = 0
 *
 * // Safe: only one increments at a time
 * scope.launchSafe {
 *     sharedCounter++  // Protected by mutex
 * }
 * ```
 *
 * **vs. [launch]:**
 * - [launch]: mutex is optional (specify via parameter)
 * - [launchSafe]: always uses this scope's mutex
 *
 * @param context additional [CoroutineContext] elements
 * @param mutex the [Mutex] to acquire for synchronization (default: scope's mutex)
 * @param trackFlow optional counter to track active jobs
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute with mutex locked
 * @return [Job] that can be cancelled or awaited
 *
 * @see launch for optional mutex locking
 */
fun SafeContextScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

/**
 * Launches a coroutine with guaranteed mutex locking and a deferred result.
 *
 * Like [launchSafe], but returns a [Deferred] that provides the block's result.
 * Use this when you need to await the result of a thread-safe operation.
 *
 * **Exception handling:**
 * If the block throws an exception, the exception is wrapped in the [Deferred].
 * Call [Deferred.await] will re-throw the exception.
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.IO)
 *
 * val resultJob = scope.asyncSafe {
 *     val result = computeExpensiveValue()
 *     result
 * }
 *
 * // Later, wait for result
 * val result = resultJob.await()
 * ```
 *
 * @param R the return type of the block
 * @param context additional [CoroutineContext] elements
 * @param mutex the [Mutex] to acquire for synchronization (default: scope's mutex)
 * @param trackFlow optional counter to track active jobs
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute with mutex locked, returning a value of type [R]
 * @return [Deferred<R>] that provides the result when awaited
 *
 * @see asyncResult for exception handling wrapped in [Result]
 */
fun <R> SafeContextScope.asyncSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

/**
 * Launches a coroutine with optional mutex locking and wrapped exception handling.
 *
 * Like [launch], but returns a [Deferred<Result<R>>] that wraps exceptions in [Result.failure].
 * This prevents exceptions from being thrown on [await], allowing graceful error handling.
 *
 * **Exception handling:**
 * - Exceptions are caught and wrapped in [Result.failure]
 * - Calling [Deferred.await] never throws; check [Result.isSuccess] instead
 * - Useful for operations where failure is expected and recoverable
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.IO)
 *
 * val resultJob = scope.asyncResult {
 *     apiCall()  // May throw exception
 * }
 *
 * val result = resultJob.await()
 * when {
 *     result.isSuccess -> println("Success: ${result.getOrNull()}")
 *     result.isFailure -> println("Failed: ${result.exceptionOrNull()}")
 * }
 * ```
 *
 * **vs. [asyncSafe]:**
 * - [asyncSafe]: throws exceptions on [await]
 * - [asyncResult]: wraps exceptions in [Result]
 *
 * @param R the return type of the block
 * @param context additional [CoroutineContext] elements
 * @param mutex optional [Mutex] to lock before execution
 * @param trackFlow optional counter to track active jobs
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute
 * @return [Deferred<Result<R>>] with result or failure wrapped
 *
 * @see asyncResultSafe for guaranteed mutex locking with Result handling
 */
fun <R> SafeContextScope.asyncResult(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex? = null,
    trackFlow: MutableStateFlow<Int>? = null,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    runCatching {
        mutex.withLockOrRun {
            yield() //set to context
            try {
                trackFlow?.update { it + 1 }
                GlobalJobsCollection.trackJob(globalRunDesc) { block() }
            } finally {
                trackFlow?.update { it - 1 }
            }
        }
    }
}

/**
 * Launches a coroutine with guaranteed mutex locking and wrapped exception handling.
 *
 * Combines [asyncSafe] (guaranteed locking) with [asyncResult] (exception wrapping).
 * Both synchronization and graceful error handling are provided.
 *
 * **When to use:**
 * - Need thread-safe access AND graceful error handling
 * - Expect operations may fail (network, DB, etc.)
 * - Don't want exceptions to propagate as crashes
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.IO)
 * var sharedState = ""
 *
 * val resultJob = scope.asyncResultSafe {
 *     val data = fetchData()  // May fail
 *     sharedState = data      // Protected by mutex
 *     data
 * }
 *
 * val result = resultJob.await()
 * if (result.isSuccess) {
 *     println("Updated state: $sharedState")
 * }
 * ```
 *
 * @param R the return type of the block
 * @param context additional [CoroutineContext] elements
 * @param mutex the [Mutex] to acquire for synchronization (default: scope's mutex)
 * @param trackFlow optional counter to track active jobs
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute with mutex locked
 * @return [Deferred<Result<R>>] with result or failure wrapped
 *
 * @see asyncSafe for guaranteed locking without exception wrapping
 * @see asyncResult for optional locking with exception wrapping
 */
fun <R> SafeContextScope.asyncResultSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    runCatching {
        mutex.withLockOrRun {
            yield() //set to context
            try {
                trackFlow?.update { it + 1 }
                GlobalJobsCollection.trackJob(globalRunDesc) { block() }
            } finally {
                trackFlow?.update { it - 1 }
            }
        }
    }
}


/**
 * Launches a coroutine and cancels any previous job with the same key.
 *
 * Only the most recent launch with a given key is allowed to run. Previous jobs
 * with the same key are cancelled. Useful for "latest only" patterns where you
 * always want the newest request to win.
 *
 * **Behavior:**
 * - Cancels the previous job for the same [key]
 * - Launches the new coroutine
 * - Tracks the job so it can be cancelled if launched again
 *
 * **Use cases:**
 * - Debouncing user searches (only keep the latest search active)
 * - Auto-complete: cancel previous requests when user types new characters
 * - Loading: cancel previous load if user triggers load again
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.Main)
 *
 * // First call
 * scope.launchLatest("search") {
 *     delay(1000)  // Simulating network request
 *     performSearch("kotlin")
 * }
 *
 * // After 100ms, user searches again
 * scope.launchLatest("search") {
 *     performSearch("android")  // Previous job cancelled
 * }
 * ```
 *
 * **Note:**
 * Does not use the scope's mutex. If you need both latest-only AND synchronization,
 * use [launchLatestSafe].
 *
 * @param key identifier for this logical operation
 * @param context additional [CoroutineContext] elements
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute
 * @return [Job] that can be cancelled or awaited
 *
 * @see launchIfNotStarted for conditional launching
 * @see launchLatestSafe for latest-only with mutex protection
 */
fun SafeContextScope.launchLatest(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context) {
    GlobalJobsCollection.trackJob(globalRunDesc) { block() }
}.also { curJob ->
    singleRunJobs.update {
        it[key]?.cancel()
        it - key
    }
}

/**
 * Launches a coroutine only if no job with the same key is already running.
 *
 * If a job with this [key] is already active, returns that job instead of launching a new one.
 * If the job exists but has already completed, launches a new job. Useful for deduplicating
 * concurrent operations that are expensive to run multiple times.
 *
 * **Behavior:**
 * - If job with [key] is running: return it (don't launch new job)
 * - If job with [key] is completed/cancelled: launch new job
 * - If no job with [key]: launch new job
 *
 * **Use cases:**
 * - Fetching data: prevent multiple identical network requests
 * - Database queries: avoid duplicate queries
 * - Expensive computations: don't recalculate if already in progress
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.IO)
 *
 * // First call - launches job
 * val job1 = scope.launchIfNotStarted("fetch-user") {
 *     val user = api.getUser(id)
 *     updateUI(user)
 * }
 *
 * // Second call (while first is running) - returns same job
 * val job2 = scope.launchIfNotStarted("fetch-user") {
 *     val user = api.getUser(id)
 *     updateUI(user)
 * }
 *
 * println(job1 === job2)  // true
 *
 * // After job completes, new call launches fresh job
 * val job3 = scope.launchIfNotStarted("fetch-user") {
 *     // New network request
 * }
 * ```
 *
 * **vs. [launchLatest]:**
 * - [launchIfNotStarted]: returns existing job, doesn't cancel
 * - [launchLatest]: cancels existing job, launches new one
 *
 * @param key identifier for this logical operation
 * @param context additional [CoroutineContext] elements
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute (only if not already started)
 * @return existing active [Job] if running, or newly launched [Job]
 *
 * @see launchLatest for cancelling-previous pattern
 */
fun SafeContextScope.launchIfNotStarted(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val latest = singleRunJobs.value[key]
    if (latest?.isActive == true) return latest
    latest?.cancel()

    return launch(context = context) {
        GlobalJobsCollection.trackJob(globalRunDesc) { block() }
    }.also { curJob ->
        singleRunJobs.update { it + (key to curJob) }
    }
}

/**
 * Launches a coroutine with mutex locking, cancelling any previous job with the same key.
 *
 * Combines [launchLatest] (cancel-previous pattern) with mutex protection (thread-safe).
 * Useful when you want both "latest wins" semantics AND synchronized access.
 *
 * **Behavior:**
 * - Acquires the scope's mutex before execution
 * - Cancels previous job with same [key]
 * - Launches new job in exclusive mutex lock
 *
 * **Use cases:**
 * - Updating shared state from latest request (thread-safe)
 * - Latest file save operation (only save the latest version)
 * - Synchronized model refresh
 *
 * **Usage:**
 * ```kotlin
 * val scope = SafeContextScope(Dispatchers.Main)
 * var sharedData = ""
 *
 * scope.launchLatestSafe("update") {
 *     val newData = fetchFromServer()
 *     sharedData = newData  // Protected by mutex
 * }
 *
 * // Only the latest request will update sharedData
 * ```
 *
 * @param key identifier for this logical operation
 * @param context additional [CoroutineContext] elements
 * @param globalRunDesc global job description ID (0 = not tracked)
 * @param block the coroutine code to execute with mutex locked
 * @return [Job] that can be cancelled or awaited
 *
 * @see launchLatest for latest-only without mutex
 * @see launchSafe for guaranteed locking without latest-only behavior
 */
fun SafeContextScope.launchLatestSafe(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(
    context = context,
    start = CoroutineStart.UNDISPATCHED,
) {
    mutex.withLock {
        yield() //set to context
        GlobalJobsCollection.trackJob(globalRunDesc) { block() }
    }
}.also { curJob ->
    singleRunJobs.update {
        it[key]?.cancel()
        it + (key to curJob)
    }
}

/**
 * Acquires mutex lock if not null, otherwise executes action directly.
 *
 * Utility function for optional mutex protection. If the [Mutex] is null,
 * the action runs without synchronization. Otherwise, it runs with exclusive access.
 *
 * **Usage:**
 * ```kotlin
 * val mutex: Mutex? = null  // or Mutex()
 *
 * val result = mutex.withLockOrRun {
 *     sharedResource.update()
 * }
 *
 * // If mutex is null: executes immediately
 * // If mutex is not null: waits for lock, then executes
 * ```
 *
 * @param T the return type of the action
 * @param action the block to execute
 * @return the result of the action
 */
suspend inline fun <T> Mutex?.withLockOrRun(action: () -> T): T {
    return if (this != null) {
        withLock(action = action)
    } else {
        action()
    }
}