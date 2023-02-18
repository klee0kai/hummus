package com.github.klee0kai.android_devkit.collections.weaklist

import com.github.klee0kai.android_devkit.model.IProvide
import com.github.klee0kai.android_devkit.model.RefProvide

class WeakList<T> constructor() : RefList<T>() {

    constructor(list: Iterable<T?>) : this() {
        addAll(list)
    }

    override fun wrapRef(it: T?): IProvide<T?> {
        return RefProvide.weak(it)
    }

    override fun createNew(list: List<T?>): RefList<T> {
        return WeakList(list)
    }
}