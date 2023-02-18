package com.github.klee0kai.android_devkit.collections.weaklist

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.klee0kai.android_devkit.collections.contains
import com.github.klee0kai.android_devkit.model.IProvide
import java.util.*
import java.util.function.IntFunction

abstract class RefList<T> : MutableList<T?>, List<T?> {

    private val list: MutableList<IProvide<T?>> = LinkedList()

    abstract fun wrapRef(it: T?): IProvide<T?>

    abstract fun createNew(list: List<T?>): RefList<T>

    override val size: Int
        get() = list.size

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun contains(element: T?): Boolean {
        return list.contains { it.get() == element }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun <T : Any?> toArray(generator: IntFunction<Array<T>>): Array<T> {
        return list.map { it.get() }.toArray(generator)
    }


    override fun add(element: T?): Boolean {
        clearNulls()
        return list.add(wrapRef(element))
    }


    override fun remove(o: T?): Boolean {
        return clearNulls(o)
    }

    override fun containsAll(c: Collection<T?>): Boolean {
        return toStrongList().containsAll(c)
    }

    override fun addAll(collection: Collection<T?>): Boolean {
        clearNulls(null)
        var added = false
        for (c in collection) {
            added = added or list.add(wrapRef(c))
        }
        return added
    }

    override fun addAll(index: Int, collection: Collection<T?>): Boolean {
        var index = index
        var added = false
        for (c in collection) {
            list.add(index++, wrapRef(c))
            added = true
        }
        return added
    }

    override fun removeAll(collection: Collection<T?>): Boolean {
        val it = list.iterator()
        var removed = false
        while (it.hasNext()) {
            val ref = it.next()
            if (ref.get() == null || collection.contains(ref.get())) {
                it.remove()
                removed = true
            }
        }
        return removed
    }

    override fun retainAll(collection: Collection<T?>): Boolean {
        val it = list.iterator()
        var removed = false
        while (it.hasNext()) {
            val ref = it.next()
            if (ref.get() == null || !collection.contains(ref.get())) {
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val refList = o as RefList<*>
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
        private val iterator: MutableListIterator<IProvide<T?>>
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