package com.github.klee0kai.android_devkit.model

data class Group<K, T>(
    val key: K,
    val items: List<T>,
)
