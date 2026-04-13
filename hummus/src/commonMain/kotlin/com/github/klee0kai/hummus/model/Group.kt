package com.github.klee0kai.hummus.model

data class Group<K, T>(
    val key: K,
    val items: List<T>,
)
