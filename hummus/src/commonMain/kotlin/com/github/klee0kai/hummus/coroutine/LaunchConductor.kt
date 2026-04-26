package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.yield

/**
 * Coordinates parallel coroutine work to ensure they complete together.
 *
 * Useful for orchestrating multiple parallel tasks where you want to wait for all of them
 * to complete before proceeding. Similar to `launch` aggregation in structured concurrency.
 *
 * **How it works:**
 * - Each call to [finishTogether] increments a counter
 * - The suspend block executes with the counter incremented
 * - When the block completes (finally), the counter is decremented
 * - If the counter goes to 0, all parallel work is complete
 * - The function waits for the counter to reach 0 before returning
 *
 * **Usage example:**
 *
 * Orchestrating multiple parallel symbol processors:
 * ```kotlin
 * class TargetKSPProcessor(val targetProcessors: Array<TargetSymbolProcessor>) {
 *     override fun process(resolver: Resolver): List<KSAnnotated> = runBlocking {
 *         val launchConductor = LaunchConductor()
 *
 *         val generateCodeJob = launch {
 *             // Each processor runs concurrently
 *             targetProcessors.forEach { processor ->
 *                 launch {
 *                     val results = launchConductor.finishTogether {
 *                         // Wait for all processors to find symbols
 *                         processor.findSymbolsToProcess(resolver)
 *                     }
 *                     // Process results
 *                     processor.process(...)
 *                 }
 *             }
 *         }
 *
 *         // Waits for all finishTogether blocks to complete
 *         generateCodeJob.join()
 *     }
 * }
 * ```
 *
 * **Key behavior:**
 * - Non-blocking: doesn't block threads, only suspends coroutines
 * - Counter-based: tracks active work via atomic integer
 * - Synchronization: uses StateFlow to coordinate completion
 *
 * @see kotlinx.coroutines.launch for structured concurrency
 * @see kotlinx.coroutines.awaitAll for alternative group completion
 */
class LaunchConductor {

    private val parallelWorkCounter = MutableStateFlow(0)
    private val _runCount = MutableStateFlow(0)
    val runCount = _runCount.asStateFlow()

    /**
     * Executes a suspend block and waits for all parallel work to finish.
     *
     * When this function is called:
     * 1. Increments the work counter
     * 2. Executes the suspend block
     * 3. Decrements the counter (in finally, even on exception)
     * 4. If this was the last work, returns to the caller
     * 5. If other work is still active, suspends until counter reaches 0
     *
     * **Thread safety:**
     * - Safe to call from multiple coroutines concurrently
     * - Uses StateFlow for thread-safe counter updates
     *
     * **Exception handling:**
     * - If the block throws, counter is still decremented
     * - Exception is re-thrown after cleanup
     * - Waiting is not affected by exceptions in other blocks
     *
     * @param T the return type of the suspend block
     * @param suspendFun the suspend block to execute
     * @return the result from suspendFun
     */
    suspend fun <T> finishTogether(
        suspendFun: suspend () -> T,
    ): T {
        parallelWorkCounter.update { it + 1 }
        try {
            yield()
            return suspendFun()
        } finally {
            _runCount.update { it + 1 }
            parallelWorkCounter.update { it - 1 }
            parallelWorkCounter.first { it <= 0 }
        }
    }

}