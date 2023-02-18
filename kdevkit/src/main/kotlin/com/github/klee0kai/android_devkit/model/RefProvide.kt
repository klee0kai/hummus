package com.github.klee0kai.android_devkit.model

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


}