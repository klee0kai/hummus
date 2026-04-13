package com.github.klee0kai.hummus.model

import kotlin.reflect.KProperty

fun interface Provider<T> {

    fun get(): T

}

operator fun <T> Provider<T>.getValue(t: Any?, property: KProperty<*>): T? = get()


inline operator fun <T, R> Provider<T>.invoke(
    crossinline body: T.() -> R,
): R {
    return get().run(body)
}


