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

/**
 * A lazy state flow that recomputes its value on demand.
 *
 * Combines [MutableStateFlow] with [TouchableFlow] to create a state that updates
 * only when explicitly touched. The update block runs lazily in a coroutine scope,
 * and the computation is cancelled and restarted if touched again before completion.
 *
 * **Key features:**
 * - **Lazy computation**: Update block only runs when there are collectors
 * - **Touchable**: [touch] method triggers recomputation with a specific argument
 * - **Hot flow**: Like StateFlow, multiple collectors share the same value
 * - **Cancellable**: [cancelTouch] cancels any in-progress update
 *
 * **Computation lifecycle:**
 * 1. Flow is created with initial value
 * 2. On first collector: update block runs with [defaultArg]
 * 3. On [touch]: update block reruns with provided [Arg]
 * 4. On last collector unsubscribe: computation is cancelled
 *
 * **Usage example:**
 * ```kotlin
 * val userState: LazyStateFlow<User, String> = lazyStateFlow(
 *     init = User.empty(),
 *     defaultArg = "guest",
 *     scope = viewModelScope,
 *     block = { userId ->
 *         // Runs on first subscription and on each touch(userId)
 *         val user = fetchUser(userId)
 *         value = user
 *         emit(user)
 *     }
 * )
 *
 * // In UI:
 * userState.collect { user -> updateUI(user) }
 *
 * // Refresh with new ID
 * userState.touch("user123")
 *
 * // Cancel ongoing refresh
 * userState.cancelTouch()
 * ```
 *
 * @param T the type of the state value
 * @param Arg the type of argument for the [touch] method
 *
 * @see lazyStateFlow factory function
 * @see cancelTouch to cancel in-progress computation
 */
interface LazyStateFlow<T, in Arg> : MutableStateFlow<T>, TouchableFlow<T, Arg> {

    /**
     * Cancels the current update operation.
     *
     * If an update block is currently running, this cancels it immediately.
     * The state value is not changed, and the next [touch] or collector
     * subscription will trigger a new update.
     *
     * **Effects:**
     * - Cancels any in-progress update block
     * - Does not affect the current state value
     * - Next [touch] will start fresh computation
     *
     * **Usage:**
     * ```kotlin
     * val state: LazyStateFlow<Data, Unit> = ...
     * state.touch()        // Start update
     * // ... do some work
     * state.cancelTouch()  // Cancel the update
     * ```
     */
    fun cancelTouch()

}

/**
 * Creates a lazy state flow that updates on demand.
 *
 * Produces a [LazyStateFlow] that holds a mutable state and recomputes it only when
 * explicitly triggered via [touch] or on first subscription. The update block runs
 * in the provided coroutine scope and has full access to modify the state.
 *
 * **Lazy evaluation:**
 * - Update block doesn't run until there's a collector
 * - Automatically cancels computation when last collector unsubscribes
 * - Can be restarted with [LazyStateFlow.touch]
 *
 * **Default argument:**
 * The [defaultArg] is used:
 * - On first subscription (when collectors arrive)
 * - After [touch] is called and the argument is consumed
 * - This resets the argument for the next automatic update
 *
 * **State modifications:**
 * The update block receives [MutableStateFlow] as receiver and can:
 * - Call [MutableStateFlow.value] to read current state
 * - Call [MutableStateFlow.emit] to emit new values
 * - Call [MutableStateFlow.update] to transform state
 *
 * **Error handling:**
 * If the update block throws an exception:
 * - The state is not modified
 * - The exception propagates through the flow
 * - Next [touch] or subscription will retry
 *
 * **Usage examples:**
 *
 * Simple refresh flow:
 * ```kotlin
 * val userFlow = lazyStateFlow(
 *     init = User.empty(),
 *     defaultArg = Unit,
 *     scope = viewModelScope,
 *     block = {
 *         val user = api.getUser()
 *         value = user
 *     }
 * )
 *
 * // Collect and listen for updates
 * userFlow.collect { user -> updateUI(user) }
 *
 * // Force refresh
 * userFlow.touch()
 * ```
 *
 * Flow with argument (e.g., search):
 * ```kotlin
 * val searchResults = lazyStateFlow(
 *     init = emptyList<SearchResult>(),
 *     defaultArg = "",
 *     scope = viewModelScope,
 *     block = { query ->
 *         if (query.isNotEmpty()) {
 *             val results = api.search(query)
 *             value = results
 *         }
 *     }
 * )
 *
 * // Search with specific query
 * searchResults.touch("kotlin")
 * searchResults.touch("android")
 * ```
 *
 * @param T the type of state value
 * @param Arg the type of argument passed to [touch]
 * @param init initial state value returned immediately
 * @param defaultArg default argument used on first subscription and after consuming [touch] argument
 * @param scope [CoroutineScope] where the update block runs (typically viewModelScope)
 * @param block suspend lambda that updates the state. Receives the current [MutableStateFlow]
 *        and the argument. Called on first subscription and on each [touch] call.
 * @return a new [LazyStateFlow] that starts with [init] value
 *
 * @see LazyStateFlow.touch to trigger recomputation
 * @see LazyStateFlow.cancelTouch to cancel in-progress computation
 */
fun <T, Arg> lazyStateFlow(
    init: T,
    defaultArg: Arg,
    scope: CoroutineScope,
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