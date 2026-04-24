package com.github.klee0kai.hummus.coroutine

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Copyright 2017 ModelBox Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The access mode of a [ReadWriteMutex].
 *
 * Indicates whether the mutex is being used for reading or writing.
 * - [READ]: Multiple readers can hold the lock concurrently
 * - [WRITE]: Only one writer can hold the lock at a time
 */
enum class MutexMode {
    /**
     * Read mode: multiple readers allowed concurrently.
     */
    READ,
    /**
     * Write mode: exclusive access, only one writer at a time.
     */
    WRITE
}

/**
 * The lock state of a [ReadWriteMutex].
 *
 * Indicates whether a specific access mode is currently acquired.
 */
enum class MutexState {
    /**
     * The mutex is currently locked (readers or writer has lock).
     */
    LOCKED,

    /**
     * The mutex is unlocked (no active readers or writers).
     */
    UNLOCKED
}

/**
 * State information about a [ReadWriteMutex].
 *
 * Combines mode and lock state to describe the current synchronization status.
 *
 * @param mode current access mode (READ or WRITE)
 * @param state current lock state (LOCKED or UNLOCKED)
 */
data class MutexInfo(val mode: MutexMode, val state: MutexState)


/**
 * A reader-writer mutex allowing multiple concurrent readers or a single exclusive writer.
 *
 * Implements classic read-write locking semantics:
 * - **Multiple readers**: Can hold read lock simultaneously (shared access)
 * - **Single writer**: Exclusive access, blocks all readers and other writers
 * - **Writer priority**: New readers are blocked while a writer is waiting
 *
 * **Use cases:**
 * - Database caches: many readers, occasional updates
 * - Configuration management: many readers, rare writes
 * - State synchronization: concurrent reads with exclusive updates
 *
 * **Locking rules:**
 * - Multiple [withReadLock] calls can run concurrently
 * - [withWriteLock] blocks readers and other writers
 * - While writer is waiting, new readers are also blocked (writer starvation prevention)
 *
 * **State tracking:**
 * The [state] property reflects current lock status:
 * - [state.mode]: READ or WRITE
 * - [state.state]: LOCKED or UNLOCKED
 * - Use [subscribe] to listen for state changes
 *
 * **Thread safety:**
 * All operations are thread-safe and suspend-safe. Designed for use in coroutines.
 *
 * **Example:**
 * ```kotlin
 * val cache = ReadWriteMutex()
 * var data = mapOf<String, String>()
 *
 * // Many threads/coroutines can read concurrently
 * cache.withReadLock {
 *     println(data)  // Non-exclusive read access
 * }
 *
 * // Exclusive write access
 * cache.withWriteLock {
 *     data = data.plus("key" to "value")  // Only this writer runs
 * }
 * ```
 *
 * **Comparison to alternatives:**
 * - [Mutex]: Simple mutual exclusion, no reader optimization
 * - [ReadWriteMutex]: Optimized for read-heavy workloads
 * - ReentrantReadWriteLock (Java): Similar semantics
 *
 * @see withReadLock
 * @see withWriteLock
 * @see state property for monitoring
 */
class ReadWriteMutex {
    /**
     * Controls whether new readers can be admitted.
     * Locked when a writer is waiting or executing.
     */
    private val allowNewReads = Mutex()

    /**
     * Controls exclusive write access.
     * Locked to block new writers and wait for readers to drain.
     */
    private val allowNewWrites = Mutex()

    /**
     * Counter of currently active readers.
     */
    private val readers = atomic(0)

    /**
     * Protects access to [readers] counter.
     */
    private val stateLock = Mutex()

    /**
     * Protects the [stateListeners] list.
     */
    private val stateListenersMutex = Mutex()
    /**
     * Listeners notified when [state] changes.
     */
    private val stateListeners = mutableListOf<suspend (MutexInfo) -> Unit>()

    /**
     * Current lock state (mode and lock status).
     * Updated whenever lock state changes. Observable via [subscribe].
     */
    var state = MutexInfo(MutexMode.READ, MutexState.UNLOCKED)
        private set

    /**
     * Acquires the read lock and executes the block.
     *
     * Allows multiple concurrent readers. The read lock is acquired before the block
     * executes and released when it completes (even on exception).
     *
     * **Semantics:**
     * - Multiple [withReadLock] calls can run concurrently (shared access)
     * - Blocked if [withWriteLock] is currently running or waiting
     * - Writers can be starved if readers keep arriving
     *
     * **Exception handling:**
     * If [block] throws, the exception is re-thrown and lock is released.
     *
     * **Usage:**
     * ```kotlin
     * val rw = ReadWriteMutex()
     * val result = rw.withReadLock {
     *     // Read shared data
     *     getData()  // Multiple threads can do this simultaneously
     * }
     * ```
     *
     * **Interaction with writes:**
     * - If writer is active: blocks until write completes
     * - If writer is waiting: blocks to let writer proceed (writer priority)
     * - If only readers: proceeds immediately (shared access)
     *
     * @param T return type of the block
     * @param block suspend function with read access
     * @return result from the block
     *
     * @see withWriteLock for exclusive write access
     */
    suspend fun <T> withReadLock(block: suspend () -> T): T {
        return try {
            // Ensure new readers are allowed
            allowNewReads.withLock {
                stateLock.withLock {
                    // Increment the reader count.
                    if (readers.getAndIncrement() <= 0) {
                        // If we're the first reader, ensure that writes are locked out
                        allowNewWrites.lock(this)
                        // Invoke user callback
                        state = MutexInfo(MutexMode.READ, MutexState.LOCKED)
                        notifyListeners()
                    }
                }
            }
            // Execute the user function.
            block()
        } finally {
            // We don't want to use allowNewReads here, because a writer can acquire that lock
            // while waiting fol allowNewWrites to be unlocked.  Instead, we'll just treat clean-up
            // like we're draining all outstanding readers to admit the writer.
            stateLock.withLock {
                // Decrement the reader count, and unlock if we were the last reader
                if (readers.decrementAndGet() <= 0) {
                    try {
                        // Invoke user callback in opposite order from above
                        state = MutexInfo(MutexMode.READ, MutexState.UNLOCKED)
                        notifyListeners()
                    } finally {
                        // If a writer is pending, this will unlock it
                        allowNewWrites.unlock(this)
                    }
                }
            }
        }
    }

    /**
     * Acquires the write lock and executes the block.
     *
     * Provides exclusive access: no readers or other writers can run concurrently.
     * Also prevents new readers from starting (writer priority). The lock is acquired
     * before the block executes and released when it completes (even on exception).
     *
     * **Semantics:**
     * - Exclusive access: only one [withWriteLock] can run at a time
     * - Waits for all existing readers to complete
     * - Prevents new readers from starting while writer is waiting or running
     * - Other writers are blocked
     *
     * **Exception handling:**
     * If [block] throws, the exception is re-thrown and lock is released.
     *
     * **Usage:**
     * ```kotlin
     * val rw = ReadWriteMutex()
     * rw.withWriteLock {
     *     // Exclusive write access
     *     updateSharedData()  // No readers or writers can run here
     * }
     * ```
     *
     * **Interaction with reads:**
     * - Blocks until all active readers finish
     * - New readers are blocked while writer is waiting or running
     * - Other writers are blocked
     *
     * **Writer priority:**
     * New readers cannot start while a writer is waiting or running.
     * This prevents reader starvation of writers (but can starve readers).
     *
     * @param T return type of the block
     * @param fn suspend function with exclusive write access
     * @return result from the block
     *
     * @see withReadLock for shared read access
     */
    suspend fun <T> withWriteLock(fn: suspend () -> T): T {
        // Prevent readers from starting any new action
        return allowNewReads.withLock {
            // Wait for all outstanding readers to drain.
            allowNewWrites.withLock {
                try {
                    state = MutexInfo(MutexMode.WRITE, MutexState.LOCKED)
                    notifyListeners()
                    fn()
                } finally {
                    state = MutexInfo(MutexMode.WRITE, MutexState.UNLOCKED)
                    notifyListeners()
                }
            }
        }
    }

    /**
     * Subscribes to state change notifications.
     *
     * Registers a listener that is called whenever the lock state changes
     * (when entering/exiting read or write lock). The listener receives
     * the new [MutexInfo] state.
     *
     * **Usage:**
     * ```kotlin
     * val rw = ReadWriteMutex()
     *
     * rw.subscribe { info ->
     *     println("Mode: ${info.mode}, State: ${info.state}")
     * }
     *
     * rw.withReadLock {
     *     // Listener called with (READ, LOCKED)
     * }
     * // Listener called with (READ, UNLOCKED)
     * ```
     *
     * **Important:**
     * Don't forget to [unsubscribe] to prevent memory leaks.
     *
     * @param listener suspend function called with updated [MutexInfo]
     *
     * @see unsubscribe
     * @see stateFlow for a [Flow]-based alternative
     */
    suspend fun subscribe(listener: suspend (MutexInfo) -> Unit) = stateListenersMutex.withLock {
        stateListeners.add(listener)
    }

    /**
     * Unsubscribes a listener from state changes.
     *
     * Removes a previously registered listener. After this call,
     * the listener will no longer receive state change notifications.
     *
     * @param listener the listener to remove (must be the same instance passed to [subscribe])
     *
     * @see subscribe
     */
    suspend fun unsubscribe(listener: suspend (MutexInfo) -> Unit) = stateListenersMutex.withLock {
        stateListeners.remove(listener)
    }

    /**
     * Internal notification of state changes to all listeners.
     */
    private suspend fun notifyListeners(): Unit = stateListenersMutex.withLock {
        stateListeners.forEach { listener ->
            listener.invoke(state)
        }
    }

}

/**
 * Converts the mutex state into a [Flow] of [MutexInfo].
 *
 * Creates a [Flow] that emits state changes of this mutex. The flow emits:
 * - The current state immediately on collection
 * - Updated state whenever lock mode or state changes
 *
 * **Usage:**
 * ```kotlin
 * val rw = ReadWriteMutex()
 *
 * rw.stateFlow().collect { info ->
 *     when {
 *         info.mode == MutexMode.WRITE && info.state == MutexState.LOCKED -> {
 *             println("Writing in progress...")
 *         }
 *         else -> println("State: ${info.mode} ${info.state}")
 *     }
 * }
 * ```
 *
 * **vs. [subscribe]:**
 * - [subscribe]: callback-based, requires manual [unsubscribe]
 * - [stateFlow]: Flow-based, auto-cleanup on collection end
 *
 * **Advantages:**
 * - Automatic unsubscribe on collector termination
 * - Composable with Flow operators (filter, map, etc.)
 * - Structured concurrency friendly
 *
 * **Note:**
 * Uses [GlobalScope] internally for cleanup (marked with @OptIn).
 *
 * @return a [Flow<MutexInfo>] emitting state changes
 *
 * @see subscribe for callback-based listening
 */
@OptIn(DelicateCoroutinesApi::class)
fun ReadWriteMutex.stateFlow() = channelFlow<MutexInfo> {
    val listener: suspend (MutexInfo) -> Unit = {
        send(it)
    }

    subscribe(listener)
    listener.invoke(state)
    awaitClose {
        GlobalScope.launch {
            unsubscribe(listener)
        }
    }

}