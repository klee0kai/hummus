@file:OptIn(ExperimentalUuidApi::class)

package com.github.klee0kai.hummus.common

import kotlinx.atomicfu.atomic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Utility for generating unique identifiers and strings.
 *
 * Provides simple mechanisms to create unique IDs and strings for testing,
 * debugging, or temporary object identification. Not suitable for cryptographic
 * purposes or where actual UUIDs are needed for data persistence.
 *
 * **Components:**
 * - [dummyId]: Simple auto-incrementing integer ID
 * - [unicString]: Random UUID-based string
 *
 * **Use cases:**
 * - Generating temporary/test object identifiers
 * - Creating unique tracking IDs for internal debugging
 * - Testing scenarios where uniqueness is needed
 * - Job identification in task tracking
 *
 * **Not suitable for:**
 * - Primary keys in databases (use proper ID generation)
 * - Cryptographic tokens (use crypto libs)
 * - Production identifiers that need persistence
 */
object Dummy {

    /**
     * Atomic counter for generating sequential dummy IDs.
     * Starts at 41 (arbitrary value).
     */
    private val dummyIdCounter = atomic(41)

    /**
     * Gets the next sequential dummy ID.
     *
     * Increments and returns a counter-based ID. Simple, fast, and guaranteed unique
     * within a single runtime instance, but not persistent or globally unique.
     *
     * **Characteristics:**
     * - Sequential integers (42, 43, 44, ...)
     * - Thread-safe (uses atomic counter)
     * - Fast (no UUID generation)
     * - Only unique within current process
     *
     * **Usage:**
     * ```kotlin
     * val taskId = Dummy.dummyId  // Gets 42 (first call)
     * val trackingId = Dummy.dummyId  // Gets 43 (second call)
     * ```
     *
     * @return next unique dummy ID
     */
    val dummyId get() = dummyIdCounter.incrementAndGet()

    /**
     * Gets a random UUID-based string.
     *
     * Generates a new random UUID string for each call. Unique with very high probability
     * even across separate runs and processes.
     *
     * **Characteristics:**
     * - Format: standard UUID string (e.g., "123e4567-e89b-12d3-a456-426614174000")
     * - Random (cryptographically random)
     * - Globally unique with very high probability
     * - Slower than [dummyId] (generates UUID)
     *
     * **Usage:**
     * ```kotlin
     * val sessionId = Dummy.unicString
     * val requestId = Dummy.unicString
     * ```
     *
     * @return new random UUID string
     */
    val unicString get() = Uuid.random().toString()

}