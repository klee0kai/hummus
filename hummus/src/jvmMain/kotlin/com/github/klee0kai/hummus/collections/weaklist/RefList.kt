package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.stone.weakref.Ref
import java.util.*

abstract class RefList<T> : MutableList<T?>, List<T?> {

    private val list: MutableList<Ref<T?>> = LinkedList()

    abstract fun wrapRef(it: T?): Ref<T?>

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
        var index = index
        var added = false
        for (c in elements) {
            list.add(index++, wrapRef(c))
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
        return Objects.hash(list)
    }

    override fun toString(): String {
        return list.toString()
    }

    fun toStrongList(): List<T?> = list.map { it.get() }

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
    ) :
        MutableListIterator<T?> {

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