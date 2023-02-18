package com.github.klee0kai.android_devkit.model

import kotlin.reflect.KProperty

interface IProvide<T> {

    fun get(): T?

}

operator fun <T> IProvide<T>.getValue(t: Any?, property: KProperty<*>): T? = get()


inline operator fun <T, R> IProvide<T>.invoke(crossinline body: T.() -> R?): R? {
    return get()?.run(body)
}


