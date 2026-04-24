package com.github.klee0kai.hummus.ref

import com.github.klee0kai.stone.weakref.WeakRef
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakDelegate<T : Any?>(
) : ReadWriteProperty<Any?, T?> {

    private var ref: WeakRef<T?>? = null

    constructor(value: T?) : this() {
        ref = WeakRef(value)
    }

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T? = ref?.get()

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T?,
    ) {
        ref = WeakRef(value)
    }

    override fun toString(): String = "Weak( ${ref?.get()} )"

}