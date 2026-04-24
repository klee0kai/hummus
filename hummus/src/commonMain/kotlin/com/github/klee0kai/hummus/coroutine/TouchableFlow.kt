package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.flow.Flow


/**
 * A Flow that can be manually triggered to update its data.
 *
 * Extends [Flow] with a [touch] method that allows external triggers to refresh or update
 * the flow's data. This is useful for implementing cold flows that compute data on-demand
 * when explicitly requested.
 *
 * **Use cases:**
 * - Refreshing data when a user pulls-to-refresh
 * - Recomputing expensive calculations on demand
 * - Controlling when a cold flow starts emitting values
 *
 * **Difference from Hot vs Cold Flows:**
 * - **Cold Flow** (like [Flow]): Computation starts when collected
 * - **TouchableFlow**: Cold flow that can be manually triggered to re-run computation
 * - **Hot Flow** (like StateFlow): Always running, shared between collectors
 *
 * **Usage example:**
 * ```kotlin
 * val userFlow: LazyStateFlow<User, Unit> = lazyStateFlow(
 *     init = User.empty(),
 *     defaultArg = Unit,
 *     scope = scope,
 *     block = { arg ->
 *         // This block re-runs each time touch() is called
 *         val user = fetchUserFromServer()
 *         emit(user)
 *     }
 * )
 *
 * // Later, trigger a refresh
 * userFlow.touch()  // Re-runs the computation block
 * ```
 *
 * @param T the type of values emitted by the flow
 * @param Arg the type of argument passed to [touch]
 *
 * @see lazyStateFlow
 */
interface TouchableFlow<out T, in Arg> : Flow<T> {

    /**
     * Triggers the flow to update its data with the given argument.
     *
     * Signals the flow to recompute or refresh its data using the provided argument.
     * The behavior depends on the implementation (e.g., lazy evaluation, immediate refresh).
     *
     * **Thread safety:**
     * Thread safety depends on the implementation. Check the specific [TouchableFlow]
     * implementation for guarantees.
     *
     * @param arg the argument to pass to the flow's update logic
     */
    fun touch(arg: Arg)

}

/**
 * Triggers a [TouchableFlow] with Unit argument.
 *
 * Convenience function for [TouchableFlow]s that don't require an argument for updates.
 * Equivalent to calling [touch] with [Unit].
 *
 * **Usage:**
 * ```kotlin
 * val refreshFlow: TouchableFlow<Data, Unit> = ...
 * refreshFlow.touch()  // Trigger refresh without argument
 * ```
 */
fun <T> TouchableFlow<T, Unit>.touch() = touch(Unit)
