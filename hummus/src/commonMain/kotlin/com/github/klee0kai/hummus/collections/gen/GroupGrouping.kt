package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.Group

/**
 * Factory object for creating grouping transformations.
 *
 * Provides standard grouping functions for use with [group] and similar collection operations.
 * Each function has a specific transformation strategy for grouped items.
 *
 * **Typical usage with the group() extension:**
 * ```kotlin
 * users.group(
 *     groupId = { it.department },
 *     grouping = GroupGrouping.groupingToGroups()
 * )
 * ```
 *
 * @see Group for the result data class
 * @see com.github.klee0kai.hummus.collections.ListExts.group
 */
object GroupGrouping {

    /**
     * Flattens grouped items into a single list.
     *
     * For each group, returns the items as-is without wrapping.
     * The key is discarded.
     *
     * **Usage:**
     * ```kotlin
     * val items = listOf(1, 2, 3, 4, 5)
     * val result = items.group(
     *     groupId = { if (it % 2 == 0) "even" else "odd" },
     *     grouping = GroupGrouping.groupingFlatten()
     * )
     * // Result: [1, 3, 5, 2, 4] (odds, then evens)
     * ```
     *
     * @return grouping function that flattens items
     */
    fun <Key, Type> groupingFlatten(): (Key, Iterable<Type>) -> List<Type> = { _, it ->
        it.toList()
    }

    /**
     * Wraps each group in a list.
     *
     * For each group, returns a list containing the grouped items.
     * The key is discarded.
     *
     * **Usage:**
     * ```kotlin
     * val items = listOf("a", "b", "c", "d")
     * val result = items.group(
     *     groupId = { if (it < "c") "low" else "high" },
     *     grouping = GroupGrouping.groupingToLists()
     * )
     * // Result: [["a", "b"], ["c", "d"]]
     * ```
     *
     * @return grouping function that wraps each group in a list
     */
    fun <Key, Type> groupingToLists(): (Key, Iterable<Type>) -> List<List<Type>> = { _, it ->
        listOf(it.toList())
    }

    /**
     * Creates [Group] objects for each group.
     *
     * For each group, creates a [Group] instance with the key and items.
     * Empty groups are discarded.
     *
     * **Usage:**
     * ```kotlin
     * data class Person(val dept: String, val name: String)
     * val people = listOf(
     *     Person("Sales", "Alice"),
     *     Person("Sales", "Bob"),
     *     Person("IT", "Charlie")
     * )
     *
     * val groups = people.group(
     *     groupId = { it.dept },
     *     grouping = GroupGrouping.groupingToGroups()
     * )
     * // Result: [Group("Sales", [...]), Group("IT", [...])]
     * ```
     *
     * @return grouping function that creates Group objects, skipping empty groups
     */
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
