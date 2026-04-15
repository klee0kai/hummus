package com.github.klee0kai.hummus.common

/**
 * Interface for objects that can be cleaned up or reset.
 *
 * Provides a standard contract for objects that hold resources or state that should
 * be released or reset. Similar to Closeable but designed for non-throwing cleanup.
 *
 * **Purpose:**
 * - Release resources (memory, handles, connections)
 * - Reset internal state
 * - Cancel pending operations
 * - Clear caches
 *
 * **Design:**
 * - [clean] is non-throwing (doesn't declare checked exceptions)
 * - Default implementation does nothing (no-op)
 * - Can be called multiple times safely
 *
 * **vs. Closeable/AutoCloseable:**
 * - [Cleanable]: non-throwing cleanup, can be called multiple times
 * - [Closeable]: throws IOException, typically one-time use
 * - [Cleanable]: for optional resource cleanup
 * - [Closeable]: for mandatory resource management
 *
 * **Usage example:**
 *
 * Class that holds resources:
 * ```kotlin
 * class DataCache : Cleanable {
 *     private val cache = mutableMapOf<String, Data>()
 *     private val listeners = WeakList<CacheListener>()
 *
 *     fun get(key: String): Data? = cache[key]
 *
 *     override fun clean() {
 *         cache.clear()
 *         listeners.clear()
 *     }
 * }
 *
 * // Usage
 * val cache = DataCache()
 * cache.get("key")
 * cache.clean()  // Clear all data
 * ```
 *
 * Group cleanup:
 * ```kotlin
 * val objects = listOf<Cleanable>(cache1, cache2, listener)
 * objects.forEach { it.clean() }
 * ```
 *
 * **Implementation notes:**
 * - Default implementation (`= Unit`) allows extending without override
 * - Should be idempotent (safe to call multiple times)
 * - Should not throw exceptions
 * - Should not block indefinitely
 *
 * @see java.io.Closeable as an alternative for resource management
 */
interface Cleanable {

    /**
     * Cleans up resources or resets state.
     *
     * Implementations should:
     * - Release any held resources
     * - Clear caches or temporary data
     * - Cancel pending operations
     * - Reset to initial state
     *
     * **Contracts:**
     * - May be called multiple times safely
     * - Should not throw exceptions
     * - Should complete quickly
     * - Should be safe to call from any thread (if used in concurrent context)
     *
     * **Default behavior:**
     * Default implementation does nothing, so non-resource-holding classes don't need override.
     */
    fun clean() = Unit

}