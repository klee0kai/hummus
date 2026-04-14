package com.github.klee0kai.hummus.model

/**
 * Data class representing a group of items with a shared key.
 *
 * Result of grouping operations where items are organized by a common key.
 * Provides a simple container for a key and its associated items.
 *
 * **Usage example:**
 * ```kotlin
 * data class User(val id: Long, val department: String, val name: String)
 *
 * val users = listOf(
 *     User(1, "Sales", "Alice"),
 *     User(2, "Sales", "Bob"),
 *     User(3, "IT", "Charlie"),
 * )
 *
 * val grouped = users
 *     .groupBy { it.department }
 *     .map { (dept, userList) -> Group(dept, userList) }
 *
 * // Result:
 * // Group("Sales", [User(1, ...), User(2, ...)])
 * // Group("IT", [User(3, ...)])
 * ```
 *
 * Common grouping patterns:
 * ```kotlin
 * // Using the custom group() extension
 * val groups = users.group(
 *     groupId = { it.department },
 *     grouping = { key, items -> listOf(Group(key, items.toList())) }
 * )
 * ```
 *
 * @param K the type of the grouping key
 * @param T the type of items in the group
 * @property key the grouping key shared by all items
 * @property items the list of items with this key
 */
data class Group<K, T>(
    val key: K,
    val items: List<T>,
)
