package com.github.klee0kai.hummus.extensions

inline fun <reified T> T.then(
    condition: Boolean,
    block: T.() -> T,
) = if (condition) {
    block()
} else {
    this
}
