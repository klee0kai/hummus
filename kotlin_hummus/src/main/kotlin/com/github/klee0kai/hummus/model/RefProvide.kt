package com.github.klee0kai.hummus.model

import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

data class RefProvide<T>(
    val reference: Reference<T>
) : IProvide<T> {

    companion object {

        fun <T> weak(it: T): RefProvide<T> = RefProvide(WeakReference(it))

        fun <T> soft(it: T): RefProvide<T> = RefProvide(SoftReference(it))

    }

    override fun get(): T? = reference.get()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return get() == (other as? RefProvide<*>)?.get()
    }

    override fun hashCode(): Int {
        return get().hashCode()
    }

    override fun toString(): String {
        return "RefProvide(${get()})"
    }


}