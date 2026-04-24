package com.github.klee0kai.hummus.collections

import com.github.klee0kai.hummus.collections.gen.Equals

/**
 * Checks if the iterable contains any element matching the predicate.
 *
 * More convenient than `any()` when you want to use the same predicate as for finding index.
 *
 * **Usage:**
 * ```kotlin
 * val numbers = listOf(1, 2, 3, 4, 5)
 * numbers.contains { it > 3 } // true
 *
 * val names = listOf("Alice", "Bob", "Charlie")
 * names.contains { it.length > 5 } // true (Charlie)
 * ```
 *
 * @param predicate the condition to check
 * @return true if any element matches the predicate
 */
inline fun <T> Iterable<T>.contains(
    crossinline predicate: (T) -> Boolean
): Boolean = indexOfFirst(predicate) >= 0

/**
 * Removes duplicate elements based on custom equality.
 *
 * Returns a new list with duplicates removed according to the predicate.
 * Preserves order of first occurrence.
 *
 * **Default behavior:**
 * Uses [Equals.sameOrEq] which checks:
 * 1. Reference equality (===)
 * 2. [SameModel.isSame] if implemented
 * 3. Standard equality (==)
 *
 * **Usage examples:**
 *
 * Remove duplicates by identity:
 * ```kotlin
 * val items = listOf(obj1, obj1, obj2, obj2, obj3)
 * items.removeDoubles { a, b -> a === b } // [obj1, obj2, obj3]
 * ```
 *
 * Remove duplicates by ID (for objects implementing SameModel):
 * ```kotlin
 * val users = listOf(
 *     User(1, "Alice"),
 *     User(1, "Alice Updated"),
 *     User(2, "Bob")
 * )
 * users.removeDoubles() // [User(1, ...), User(2, ...)]
 * ```
 *
 * Custom comparison:
 * ```kotlin
 * val words = listOf("Hello", "hello", "HELLO", "world")
 * words.removeDoubles { a, b -> a.lowercase() == b.lowercase() }
 * // [Hello, world]
 * ```
 *
 * @param predicate custom equality check (default uses SameModel or ==)
 * @return new list with duplicates removed, preserving first occurrence order
 */
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

/**
 * Groups items by key and applies a transformation to each group.
 *
 * Flexible grouping function that allows custom transformation of each group.
 * Unlike Kotlin's standard [groupBy], this supports immediate transformation
 * and filtering at grouping time.
 *
 * **Usage examples:**
 *
 * Simple grouping:
 * ```kotlin
 * data class User(val department: String, val name: String)
 *
 * val users = listOf(
 *     User("Sales", "Alice"),
 *     User("Sales", "Bob"),
 *     User("IT", "Charlie")
 * )
 *
 * val grouped = users.group(
 *     groupId = { it.department },
 *     grouping = { key, items ->
 *         listOf(Group(key, items.toList()))
 *     }
 * )
 * // Result: [Group("Sales", [...]), Group("IT", [...])]
 * ```
 *
 * Grouping with aggregation:
 * ```kotlin
 * val totals = purchases.group(
 *     groupId = { it.category },
 *     grouping = { category, items ->
 *         val total = items.sumOf { it.amount }
 *         if (total > 0) listOf(CategoryTotal(category, total)) else null
 *     }
 * )
 * ```
 *
 * @param Key the type of grouping key
 * @param Type the type of input items
 * @param OutType the type of output items (after transformation)
 * @param groupId function to extract grouping key from item
 * @param grouping transformation function that receives key and grouped items, returns transformed items or null
 * @return list of transformed items from all groups
 */
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

/**
 * Performs a left join between two iterables.
 *
 * For each element in the left iterable, finds a matching element in the right iterable.
 * Unmatched left elements are included with null as the right element.
 *
 * **SQL equivalent:** `SELECT * FROM left LEFT JOIN right ON isJoin(left, right)`
 *
 * **Usage example:**
 * ```kotlin
 * data class Order(val id: Int, val customer: String)
 * data class Invoice(val orderId: Int, val amount: Double)
 *
 * val orders = listOf(
 *     Order(1, "Alice"),
 *     Order(2, "Bob"),
 *     Order(3, "Charlie")
 * )
 *
 * val invoices = listOf(
 *     Invoice(1, 100.0),
 *     Invoice(2, 200.0)
 *     // Order 3 has no invoice
 * )
 *
 * val result = orders.leftJoin(
 *     invoices,
 *     isJoin = { order, invoice -> order.id == invoice.orderId },
 *     join = { order, invoice ->
 *         OrderWithInvoice(order, invoice)
 *     }
 * )
 * // Result contains all 3 orders, Order(3, ...) with null invoice
 * ```
 *
 * @param T1 type of left iterable
 * @param T2 type of right iterable
 * @param OutType type of output
 * @param list the right iterable
 * @param isJoin predicate to match elements (default uses equality)
 * @param join transformation function
 * @return list of joined results, including unmatched left elements
 */
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

/**
 * Performs a right join between two iterables.
 *
 * For each element in the right iterable, finds a matching element in the left iterable.
 * Unmatched right elements are included with null as the left element.
 *
 * **SQL equivalent:** `SELECT * FROM left RIGHT JOIN right ON isJoin(left, right)`
 *
 * Note: Implemented by reversing the operands of a left join.
 *
 * @param T1 type of left iterable (this)
 * @param T2 type of right iterable
 * @param OutType type of output
 * @param list the right iterable
 * @param isJoin predicate to match elements (default uses equality)
 * @param join transformation function
 * @return list of joined results, including unmatched right elements
 */
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

/**
 * Performs an inner join between two iterables.
 *
 * Returns only elements where matches are found in both iterables.
 * Supports both one-to-one and many-to-many joins.
 *
 * **SQL equivalent:** `SELECT * FROM left INNER JOIN right ON isJoin(left, right)`
 *
 * **Usage example:**
 * ```kotlin
 * data class Author(val id: Int, val name: String)
 * data class Book(val authorId: Int, val title: String)
 *
 * val authors = listOf(Author(1, "Tolkien"), Author(2, "Martin"))
 * val books = listOf(
 *     Book(1, "LOTR"),
 *     Book(1, "The Hobbit"),
 *     Book(2, "GoT")
 * )
 *
 * // One-to-one join (first match only)
 * val firstBooks = authors.innerJoin(books, multiToMulti = false) { author, book ->
 *     "${author.name}: ${book.title}"
 * }
 *
 * // Many-to-many join (all matches)
 * val allBooks = authors.innerJoin(books, multiToMulti = true) { author, book ->
 *     "${author.name}: ${book.title}"
 * }
 * ```
 *
 * @param T1 type of left iterable
 * @param T2 type of right iterable
 * @param OutType type of output
 * @param list the right iterable
 * @param multiToMulti if true, creates all combinations of matches; if false, takes only first match
 * @param isJoin predicate to match elements (default uses equality)
 * @param join transformation function for matched pairs
 * @return list of matched results
 */
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

/**
 * Performs a full outer join between two iterables.
 *
 * Returns all elements from both iterables. Unmatched elements appear with null
 * as the opposite side. Matched elements appear with both values.
 *
 * **SQL equivalent:** `SELECT * FROM left FULL OUTER JOIN right ON isJoin(left, right)`
 *
 * **Usage example:**
 * ```kotlin
 * data class Person(val id: Int, val name: String)
 * data class Address(val personId: Int, val city: String)
 *
 * val people = listOf(Person(1, "Alice"), Person(2, "Bob"), Person(3, "Charlie"))
 * val addresses = listOf(
 *     Address(1, "NYC"),
 *     Address(2, "LA"),
 *     Address(4, "Chicago") // No person with id 4
 * )
 *
 * val result = people.fullOuterJoin(addresses) { person, address ->
 *     when {
 *         person != null && address != null -> "${person.name} in ${address.city}"
 *         person != null -> "${person.name} - no address"
 *         address != null -> "Address in ${address.city} - no person"
 *         else -> throw IllegalStateException()
 *     }
 * }
 * // Results include Alice+NYC, Bob+LA, Charlie+null, null+Chicago(4)
 * ```
 *
 * @param T1 type of left iterable
 * @param T2 type of right iterable
 * @param OutType type of output
 * @param list the right iterable
 * @param multiToMulti if true, creates all combinations of matches; if false, takes only first match
 * @param isJoin predicate to match elements (default uses equality)
 * @param join transformation function for all pairs (matched and unmatched)
 * @return list of all joined results
 */
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
