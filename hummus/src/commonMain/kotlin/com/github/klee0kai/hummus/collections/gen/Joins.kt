package com.github.klee0kai.hummus.collections.gen

/**
 * Factory object for creating join transformation functions.
 *
 * Provides standard transformations for the result of join operations.
 * Each function defines how to combine left and right elements.
 *
 * **Typical usage with join operations:**
 * ```kotlin
 * orders.leftJoin(invoices, join = Joins.pair())  // Returns Pair<Order, Invoice?>
 * ```
 *
 * @see com.github.klee0kai.hummus.collections.ListExts.leftJoin
 * @see com.github.klee0kai.hummus.collections.ListExts.innerJoin
 */
object Joins {

    /**
     * Returns only the left element, discarding the right.
     *
     * Useful when you only care about elements from the left iterable
     * but want to validate them against the right iterable.
     *
     * **Usage:**
     * ```kotlin
     * val users = listOf(user1, user2, user3)
     * val validUserIds = setOf(1, 3)
     *
     * val validUsers = users.innerJoin(
     *     validUserIds,
     *     isJoin = { user, id -> user.id == id },
     *     join = Joins.left() // Only keep User objects
     * )
     * ```
     *
     * @return function that returns left element
     */
    fun <T1, T2> left(): (T1, T2) -> T1 = { it1, _ -> it1 }

    /**
     * Returns only the right element, discarding the left.
     *
     * Useful for filtering or validating the right iterable based on
     * matches with the left iterable.
     *
     * **Usage:**
     * ```kotlin
     * val userIds = listOf(1, 2, 3)
     * val activeUsers = listOf(user1, user3, user5)
     *
     * val matchedUsers = userIds.innerJoin(
     *     activeUsers,
     *     isJoin = { id, user -> user.id == id },
     *     join = Joins.right() // Only keep matched User objects
     * )
     * ```
     *
     * @return function that returns right element
     */
    fun <T1, T2> right(): (T1, T2) -> T2 = { _, it2 -> it2 }

    /**
     * Combines left and right elements into a [Pair].
     *
     * Creates a pair of both elements, useful when you need both values
     * from the join result.
     *
     * **Usage:**
     * ```kotlin
     * data class Order(val id: Int)
     * data class Invoice(val orderId: Int, val amount: Double)
     *
     * val orders = listOf(Order(1), Order(2))
     * val invoices = listOf(Invoice(1, 100.0), Invoice(2, 200.0))
     *
     * val results = orders.innerJoin(
     *     invoices,
     *     isJoin = { order, invoice -> order.id == invoice.orderId },
     *     join = Joins.pair()
     * )
     * // Results: [Pair(Order(1), Invoice(1, ...)), ...]
     * ```
     *
     * @return function that creates Pair<T1, T2>
     */
    fun <T1, T2> pair(): (T1, T2) -> Pair<T1, T2> = { it1, it2 -> Pair(it1, it2) }
}
