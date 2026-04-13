package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.hummus.model.IProvide
import com.github.klee0kai.hummus.model.RefProvide

open class SoftList<T> constructor() : RefList<T>() {

    constructor(list: Iterable<T?>) : this() {
        addAll(list)
    }

    override fun wrapRef(it: T?): IProvide<T?> {
        return RefProvide.soft(it)
    }

    override fun createNew(list: List<T?>): RefList<T> {
        return SoftList(list)
    }
}