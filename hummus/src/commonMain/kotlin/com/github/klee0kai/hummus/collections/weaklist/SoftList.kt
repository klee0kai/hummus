package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.stone.weakref.Ref
import com.github.klee0kai.stone.weakref.SoftRef

open class SoftList<T>() : RefList<T>() {

    constructor(list: Iterable<T?>) : this() {
        addAll(list)
    }

    override fun wrapRef(it: T?): Ref<T?> = SoftRef(it)

    override fun createNew(list: List<T?>): RefList<T> = SoftList(list)
}
