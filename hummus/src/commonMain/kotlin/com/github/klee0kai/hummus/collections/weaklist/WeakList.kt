package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.stone.weakref.Ref
import com.github.klee0kai.stone.weakref.WeakRef

/**
 * A [RefList] that stores elements as weak references.
 *
 * Elements can be garbage collected immediately when no strong references exist outside the list.
 * This is the most aggressive form of weak reference storage.
 *
 * **Use cases:**
 * - Observer/listener patterns where listeners may be garbage collected
 * - Weak maps/caches that don't prevent garbage collection
 * - Tracking objects without keeping them alive
 *
 * **Example - Event listeners:**
 * ```kotlin
 * class EventBus {
 *     private val listeners = WeakList<EventListener>()
 *
 *     fun subscribe(listener: EventListener) {
 *         listeners.add(listener)
 *     }
 *
 *     fun publish(event: Event) {
 *         for (listener in listeners) {
 *             listener?.onEvent(event) // listener might be null if GC'd
 *         }
 *         listeners.clearNulls() // Clean up collected listeners
 *     }
 * }
 * ```
 *
 * **Memory behavior:**
 * - Objects are collected immediately after all strong references are released
 * - Best for temporary subscriptions or short-lived listeners
 * - More aggressive than [SoftList]
 *
 * @param T the element type
 *
 * @see SoftList for softer reference collection policy
 * @see RefList for base class with full documentation
 */
open class WeakList<T>() : RefList<T>() {

    /**
     * Creates a WeakList with initial elements.
     *
     * @param list initial elements to add to the list
     */
    constructor(list: Iterable<T?>) : this() {
        addAll(list)
    }

    /**
     * Wraps elements in weak references.
     */
    override fun wrapRef(it: T?): Ref<T?> = WeakRef(it)

    /**
     * Creates a new WeakList with the given elements.
     */
    override fun createNew(list: List<T?>): RefList<T> {
        return WeakList(list)
    }
}
