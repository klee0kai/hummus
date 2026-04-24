package com.github.klee0kai.hummus.cleanable

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CleanableDelegate<T>(
    var value: T? = null,
) : ReadWriteProperty<Any?, T?>, Cleanable {

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T? = value

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T?,
    ) {
        this.value = value
    }

    override fun toString(
    ): String = "Cleanable( $value )"

    override fun clean() {
        (value as? Cleanable)?.clean()
        value = null
    }

}