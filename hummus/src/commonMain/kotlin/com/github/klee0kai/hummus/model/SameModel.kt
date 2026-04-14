package com.github.klee0kai.hummus.model

/**
 * Interface for custom equality comparison.
 *
 * Allows objects to define their own "sameness" semantics beyond standard [equals].
 * Useful for comparing objects by specific criteria (e.g., ID instead of all fields).
 *
 * **Typical use case:**
 * Comparing data model objects by ID rather than by all properties:
 *
 * ```kotlin
 * class User(val id: Long, val name: String) : SameModel {
 *     override fun isSame(o: Any): Boolean {
 *         return o is User && id == o.id
 *     }
 * }
 * ```
 *
 * Used with collection utilities that support custom equality:
 *
 * ```kotlin
 * val users = listOf(user1, user2, user3)
 * val unique = users.removeDoubles { u1, u2 -> u1.isSame(u2) }
 * ```
 *
 * @see IdItemModel for a common implementation pattern
 */
interface SameModel {

    /**
     * Checks if this object is "same" as another according to custom logic.
     *
     * @param o the object to compare with
     * @return true if the objects are considered the same
     */
    fun isSame(o: Any): Boolean

}