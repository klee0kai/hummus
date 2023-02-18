package com.github.klee0kai.android_devkit.collections.gen

import com.github.klee0kai.android_devkit.model.Group

object GroupGrouping {

    fun <Key, Type> groupingFlatten(): (Key, Iterable<Type>) -> List<Type> = { _, it ->
        it.toList()
    }


    fun <Key, Type> groupingToLists(): (Key, Iterable<Type>) -> List<List<Type>> = { _, it ->
        listOf(it.toList())
    }

    fun <Key, Type> groupingToGroups(): (Key, Iterable<Type>) -> List<Group<Key, Type>> = { k, it ->
        val list = it.toList()
        when {
            list.isEmpty() -> {
                emptyList()
            }
            else -> {
                listOf(
                    Group(
                        key = k,
                        items = list,
                    )
                )
            }
        }
    }

}