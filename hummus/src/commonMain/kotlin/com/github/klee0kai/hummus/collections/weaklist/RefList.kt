package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.stone.weakref.Ref

/**
 * A MutableList that stores elements as weak or soft references.
 *
 * This abstract class is the base for [WeakList] and [SoftList], which allow elements
 * to be garbage collected even while stored in the list. This is useful for caches,
 * listeners, and observer patterns where you don't want to keep objects alive.
 *
 * **Memory behavior:**
 * - **WeakList**: Elements can be garbage collected immediately when no strong references exist
 * - **SoftList**: Elements are only garbage collected when memory is low
 *
 * **Key characteristics:**
 * - Implements both [MutableList<T?>] and [List<T?>]
 * - Automatically removes null references (collected elements) during operations
 * - Safe iteration: references are dereferenced on access
 * - Equality based on the underlying reference list
 *
 * **Usage example:**
 *
 * Weak list for observers (listeners can be garbage collected):
 * ```kotlin
 * val listeners: WeakList<EventListener> = WeakList()
 * listeners.add(listener1)
 * listeners.add(listener2)
 *
 * // Later, when listener1 is garbage collected...
 * val activeListeners = listeners.toStrongList() // Only listener2
 *
 * for (listener in listeners) {
 *     listener?.onEvent(event) // listener can be null if collected
 * }
 * ```
 *
 * Soft list for caching:
 * ```kotlin
 * val cache: SoftList<ExpensiveObject> = SoftList()
 * for (i in 0..1000) {
 *     cache.add(ExpensiveObject(i))
 * }
 * // Objects kept in memory as long as memory is available
 * ```
 *
 * **Important notes:**
 * - Elements can be null after retrieval if they've been garbage collected
 * - Use [toStrongList] to get all currently live elements
 * - Use [clearNulls] to remove dead references
 * - Not thread-safe; use synchronized wrappers if needed
 *
 * @param T the type of elements stored (as references)
 *
 * @see WeakList for weak reference implementation
 * @see SoftList for soft reference implementation
 */
abstract class RefList<T> : MutableList<T?>, List<T?> {

    private val list: MutableList<Ref<T?>> = mutableListOf()

    /**
     * Wraps an element in the appropriate reference type.
     *
     * Subclasses implement this to use either [WeakRef] or [SoftRef].
     *
     * @param it the element to wrap (can be null)
     * @return the wrapped reference
     */
    abstract fun wrapRef(it: T?): Ref<T?>

    /**
     * Creates a new RefList of the same type with the given elements.
     *
     * Used for operations that return new lists (e.g., [subList]).
     *
     * @param list the elements for the new list
     * @return new RefList of the same concrete type
     */
    abstract fun createNew(list: List<T?>): RefList<T>

    override val size: Int
        get() = list.size

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun contains(element: T?): Boolean {
        return list.any { it.get() == element }
    }

    override fun add(element: T?): Boolean {
        clearNulls()
        return list.add(wrapRef(element))
    }

    override fun remove(element: T?): Boolean {
        return clearNulls(element)
    }

    override fun containsAll(elements: Collection<T?>): Boolean {
        return toStrongList().containsAll(elements)
    }

    override fun addAll(elements: Collection<T?>): Boolean {
        clearNulls(null)
        var added = false
        for (c in elements) {
            added = added or list.add(wrapRef(c))
        }
        return added
    }

    override fun addAll(
        index: Int,
        elements: Collection<T?>,
    ): Boolean {
        var idx = index
        var added = false
        for (c in elements) {
            list.add(idx++, wrapRef(c))
            added = true
        }
        return added
    }

    override fun removeAll(
        elements: Collection<T?>
    ): Boolean {
        val it = list.iterator()
        var removed = false
        while (it.hasNext()) {
            val ref = it.next()
            if (ref.get() == null || elements.contains(ref.get())) {
                it.remove()
                removed = true
            }
        }
        return removed
    }

    override fun retainAll(
        elements: Collection<T?>
    ): Boolean {
        val it = list.iterator()
        var removed = false
        while (it.hasNext()) {
            val ref = it.next()
            if (ref.get() == null || !elements.contains(ref.get())) {
                it.remove()
                removed = true
            }
        }
        return removed
    }

    override fun clear() {
        list.clear()
    }

    override fun get(index: Int): T? {
        val ref = list[index]
        return ref.get()
    }

    override fun set(index: Int, element: T?): T? {
        return list.set(index, wrapRef(element)).get()
    }

    override fun add(index: Int, element: T?) {
        list.add(index, wrapRef(element))
    }

    override fun removeAt(index: Int): T? {
        val ref = list.removeAt(index)
        return ref.get()
    }

    override fun indexOf(o: T?): Int {
        return list.indexOfFirst { o == it.get() }
    }

    override fun lastIndexOf(o: T?): Int {
        return list.indexOfLast { o == it.get() }
    }

    override fun iterator(): MutableIterator<T?> {
        return ListItr(list.listIterator())
    }

    override fun listIterator(): MutableListIterator<T?> {
        return ListItr(list.listIterator())
    }

    override fun listIterator(index: Int): MutableListIterator<T?> {
        return ListItr(list.listIterator(index))
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T?> {
        return createNew(toStrongList().subList(fromIndex, toIndex))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val refList = other as RefList<*>
        return list == refList.list
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }

    override fun toString(): String {
        return list.toString()
    }

    /**
     * Converts all references to strong references.
     *
     * Dereferences all elements in the list, creating a new list with strong references.
     * Elements that have been garbage collected appear as null in the result.
     *
     * **Usage:**
     * ```kotlin
     * val weakList = WeakList(listOf(obj1, obj2, obj3))
     * // After obj2 is garbage collected
     * val strongList = weakList.toStrongList() // [obj1, null, obj3]
     * ```
     *
     * @return a new list with all elements dereferenced
     */
    fun toStrongList(): List<T?> = list.map { it.get() }

    /**
     * Removes null references and optionally a specific item.
     *
     * Cleans up collected elements (nulls) and can also remove a specific item value.
     * Useful to maintain list integrity after garbage collection.
     *
     * **Usage:**
     * ```kotlin
     * val weakList = WeakList(...)
     * weakList.clearNulls()           // Remove garbage collected items
     * weakList.clearNulls(item = oldItem) // Remove nulls and oldItem
     * ```
     *
     * @param item optional specific item to remove (in addition to nulls)
     * @return true if any items were removed
     */
    fun clearNulls(item: T? = null): Boolean {
        val it = list.iterator()
        var removed = false
        while (it.hasNext()) {
            val ref = it.next()
            if (ref.get() == null || ref.get() == item) {
                it.remove()
                removed = true
            }
        }
        return removed
    }

    private inner class ListItr(
        private val iterator: MutableListIterator<Ref<T?>>
    ) : MutableListIterator<T?> {

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): T? = iterator.next().get()

        override fun hasPrevious(): Boolean = iterator.hasPrevious()

        override fun previous(): T? = iterator.previous().get()

        override fun nextIndex(): Int = iterator.nextIndex()

        override fun previousIndex(): Int = iterator.previousIndex()

        override fun remove() = iterator.remove()

        override fun set(t: T?) = iterator.set(wrapRef(t))

        override fun add(t: T?) = iterator.add(wrapRef(t))
    }
}
