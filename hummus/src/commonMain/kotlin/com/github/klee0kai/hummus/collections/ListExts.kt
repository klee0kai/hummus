package com.github.klee0kai.hummus.collections

import com.github.klee0kai.hummus.collections.gen.Equals

inline fun <T> Iterable<T>.contains(
    crossinline predicate: (T) -> Boolean
): Boolean = indexOfFirst(predicate) >= 0


fun <T> Iterable<T>.removeDoubles(
    predicate: (T, T) -> Boolean = Equals.sameOrEq()
): List<T> {
    val out = mutableListOf<T>()
    for (item in this) {
        val contains = out.contains { predicate.invoke(item, it) }
        if (!contains) out.add(item)
    }
    return out
}

fun <Key, Type, OutType> Iterable<Type>.group(
    groupId: (Type) -> Key,
    grouping: (Key, Iterable<Type>) -> Iterable<OutType>?,
): List<OutType> {
    val keys = mutableListOf<Key>()
    val groups: MutableMap<Key, MutableList<Type>?> = mutableMapOf()
    for (it in this) {
        val key: Key = groupId(it)
        var gr: MutableList<Type>? = groups[key]
        if (gr == null) {
            gr = mutableListOf<Type>()
            groups[key] = gr
            keys.add(key)
        }
        gr.add(it)
    }

    val out: MutableList<OutType> = mutableListOf<OutType>()
    for (key in keys) {
        val g: List<OutType>? = grouping.invoke(key, groups[key]!!)?.toList()
        if (g?.isNotEmpty() == true) {
            out.addAll(g)
        }
    }
    return out
}

fun <T1, T2, OutType> Iterable<T1>.leftJoin(
    list: Iterable<T2>,
    isJoin: (T1, T2) -> Boolean = Equals.sameOrEq(),
    join: (T1, T2?) -> OutType,
): List<OutType> {
    val out = mutableListOf<OutType>()
    for (it1 in this) {
        if (it1 == null) continue
        var added = false
        for (it2 in list) if (it2 != null && isJoin(it1, it2)) {
            out.add(join(it1, it2))
            added = true
            break
        }
        if (!added) {
            out.add(join(it1, null))
        }
    }
    return out
}

fun <T1, T2, OutType> Iterable<T1>.rightJoin(
    list: Iterable<T2>,
    isJoin: (T1, T2) -> Boolean = Equals.sameOrEq(),
    join: (T1?, T2) -> OutType,
): List<OutType> {
    return list.leftJoin(
        list = this,
        isJoin = { it1, it2 -> isJoin(it2, it1) },
        join = { it1, it2 -> join(it2, it1) }
    )
}

fun <T1, T2, OutType> Iterable<T1>.innerJoin(
    list: Iterable<T2>,
    multiToMulti: Boolean = false,
    isJoin: (T1, T2) -> Boolean = Equals.sameOrEq(),
    join: (T1, T2) -> OutType,
): List<OutType> {
    val out = mutableListOf<OutType>()
    for (it1 in this) {
        if (it1 == null) continue
        for (it2 in list) if (it2 != null && isJoin(it1, it2)) {
            out.add(join(it1, it2))
            if (!multiToMulti) break
        }
    }
    return out
}

fun <T1, T2, OutType> Iterable<T1>.fullOuterJoin(
    list: Iterable<T2>,
    multiToMulti: Boolean = false,
    isJoin: (T1, T2) -> Boolean = Equals.sameOrEq(),
    join: (T1?, T2?) -> OutType,
): List<OutType> {
    val out = mutableListOf<OutType>()
    val leftList = this.toList()
    val rightList = list.toList()
    val matchedRight = BooleanArray(rightList.size)
    val matchedLeft = BooleanArray(leftList.size)

    for ((leftIdx, it1) in leftList.withIndex()) {
        if (it1 == null) continue
        for ((rightIdx, it2) in rightList.withIndex()) {
            if (it2 != null && isJoin(it1, it2)) {
                out.add(join(it1, it2))
                matchedRight[rightIdx] = true
                matchedLeft[leftIdx] = true
                if (!multiToMulti) break
            }
        }
    }

    for ((leftIdx, it1) in leftList.withIndex()) {
        if (it1 != null && !matchedLeft[leftIdx]) {
            out.add(join(it1, null))
        }
    }

    for ((rightIdx, it2) in rightList.withIndex()) {
        if (it2 != null && !matchedRight[rightIdx]) {
            out.add(join(null, it2))
        }
    }

    return out
}
