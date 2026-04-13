package com.github.klee0kai.hummus.collections.gen

object Joins {

    fun <T1, T2> left(): (T1, T2) -> T1 = { it1, _ -> it1 }

    fun <T1, T2> right(): (T1, T2) -> T2 = { _, it2 -> it2 }

    fun <T1, T2> pair(): (T1, T2) -> Pair<T1, T2> = { it1, it2 -> Pair(it1, it2) }
}