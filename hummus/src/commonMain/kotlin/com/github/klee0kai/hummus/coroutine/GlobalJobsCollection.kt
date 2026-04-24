package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.common.Dummy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Global registry for tracking active jobs and long-running operations.
 *
 * Provides a centralized place to monitor all jobs registered with a description ID.
 * Useful for:
 * - Displaying global loading indicators
 * - Preventing app termination during critical operations
 * - Monitoring background work across the entire application
 * - Debugging and profiling job execution
 *
 * **Architecture:**
 * - [globalJobs]: StateFlow containing list of currently tracked jobs
 * - [trackJob]: Wraps operations to add/remove them from tracking
 * - Jobs are identified by [GlobalJob.descriptionRes] (resource ID or custom ID)
 *
 * **Usage example:**
 *
 * Track a data sync operation:
 * ```kotlin
 * GlobalJobsCollection.trackJob(R.string.sync_in_progress) {
 *     // This operation is tracked while it runs
 *     syncDatabase()
 * }
 *
 * // Observe active jobs
 * GlobalJobsCollection.globalJobs.collect { jobs ->
 *     if (jobs.isNotEmpty()) {
 *         showGlobalLoadingIndicator()
 *     }
 * }
 * ```
 *
 * Display currently running operations:
 * ```kotlin
 * GlobalJobsCollection.globalJobs.collect { jobs ->
 *     jobs.forEach { job ->
 *         println("Running: ${job.descriptionRes}")
 *     }
 * }
 * ```
 *
 * **Description ID convention:**
 * - Non-zero: Identifies the operation for logging/UI (typically Android string resource ID)
 * - Zero: Operation is not tracked (runs without adding to [globalJobs])
 *
 * **Thread safety:**
 * Uses StateFlow internally, so updates are thread-safe.
 *
 * @see GlobalJob for tracked job information
 * @see trackJob for wrapping operations
 */
object GlobalJobsCollection {

    /**
     * StateFlow of all currently active tracked jobs.
     *
     * Emits updated lists whenever jobs are added or removed.
     * Safe to collect from multiple subscribers. Empty list means no jobs are running.
     *
     * **Usage:**
     * ```kotlin
     * GlobalJobsCollection.globalJobs.collect { jobs ->
     *     isLoading = jobs.isNotEmpty()
     * }
     * ```
     */
    val globalJobs = MutableStateFlow(emptyList<GlobalJob>())

    /**
     * Wraps an operation to track it in [globalJobs].
     *
     * Automatically adds the operation to [globalJobs] before execution and removes it after.
     * If [descriptionRes] is 0, the operation runs without tracking.
     *
     * **Behavior:**
     * 1. If [descriptionRes] != 0: adds [GlobalJob] to [globalJobs]
     * 2. Executes the [block]
     * 3. Removes the job (always, even on exception)
     * 4. Exception is re-thrown
     *
     * **Usage:**
     * ```kotlin
     * val result = GlobalJobsCollection.trackJob(R.string.loading_user) {
     *     api.fetchUser()
     * }
     *
     * // Job is automatically tracked while fetchUser runs
     * ```
     *
     * **Exception handling:**
     * If [block] throws, the job is still removed and exception is re-thrown.
     *
     * @param R the return type of the block
     * @param descriptionRes ID for this operation (0 = don't track)
     * @param block the operation to execute
     * @return the result from [block]
     *
     * @throws Exception if [block] throws
     */
    inline fun <R> trackJob(
        descriptionRes: Int = 0,
        block: () -> R,
    ): R {
        val runId = Dummy.dummyId
        try {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs + listOf(
                        GlobalJob(
                            unicId = runId,
                            descriptionRes = descriptionRes
                        )
                    )
                }
            }
            return block()
        } finally {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs.filter { it.unicId != runId }
                }
            }
        }
    }

}

/**
 * Information about a tracked job in [GlobalJobsCollection].
 *
 * Represents a single operation being tracked globally. Used to identify
 * and monitor long-running or critical operations.
 *
 * **Properties:**
 * - [unicId]: Unique identifier for this job instance
 * - [descriptionRes]: Resource ID or custom ID describing the operation
 *
 * **Usage:**
 * ```kotlin
 * GlobalJobsCollection.globalJobs.collect { jobs ->
 *     jobs.forEach { job ->
 *         println("Job ${job.unicId}: ${job.descriptionRes}")
 *     }
 * }
 * ```
 *
 * @param unicId unique identifier for this job instance (assigned automatically)
 * @param descriptionRes operation identifier (typically Android string resource ID or custom ID)
 *
 * @see GlobalJobsCollection
 */
data class GlobalJob(
    val unicId: Int = 0,
    val descriptionRes: Int = 0,
)