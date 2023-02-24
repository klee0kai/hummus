package com.github.klee0kai.hummus.extensions

import com.github.klee0kai.hummus.model.ICloneable

fun <T : Any> T.tryClone(): T? {
    return (this as? ICloneable)?.clone() as? T
}