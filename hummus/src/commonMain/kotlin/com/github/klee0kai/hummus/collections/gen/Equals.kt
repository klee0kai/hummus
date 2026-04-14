package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.SameModel
import kotlin.reflect.KClass

/**
 * Factory object for creating equality predicates.
 *
 * Provides different comparison strategies for use with collection utilities
 * like [group], [leftJoin], [removeDoubles], etc.
 *
 * Each function returns a predicate that can be customized or composed.
 *
 * @see SameModel for custom equality semantics
 * @see removeDoubles for deduplication examples
 */
object Equals {

    /**
     * Creates a predicate that uses standard equality (==).
     *
     * **Usage:**
     * ```kotlin
     * val predicate = Equals.objectEq<String, String>()
     * val list = listOf("a", "b", "a")
     * val unique = list.removeDoubles(predicate)
     * ```
     *
     * @return predicate checking `it1 == it2`
     */
    fun <T1, T2> objectEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1 == it2
    }

    /**
     * Creates a predicate that uses reference equality (===).
     *
     * Checks if both arguments are the exact same object in memory.
     * Ignores equality overrides.
     *
     * **Usage:**
     * ```kotlin
     * val list = mutableListOf("a")
     * val same = list.contains { it === list[0] } // Reference check
     * ```
     *
     * @return predicate checking `it1 === it2`
     */
    fun <T1, T2> linkEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1 === it2
    }

    /**
     * Creates a predicate that checks class/type equality.
     *
     * Returns true only if both objects are instances of exactly the same class.
     *
     * **Usage:**
     * ```kotlin
     * class Animal
     * class Dog : Animal()
     *
     * val dog = Dog()
     * val animal = dog as Animal
     *
     * typeEq<Any, Any>()(dog, animal) // false - different classes
     * ```
     *
     * @return predicate checking `it1::class === it2::class`
     */
    fun <T1 : Any, T2 : Any> typeEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1::class === it2::class
    }

    /**
     * Creates a composite predicate with flexible equality.
     *
     * Checks equality in this order:
     * 1. Reference equality (===) - same object in memory
     * 2. [SameModel.isSame] - if it1 implements SameModel
     * 3. Standard equality (==) - fallback to equals()
     *
     * This is the default for join and grouping operations.
     *
     * **Usage examples:**
     * ```kotlin
     * // For objects with custom SameModel
     * data class User(override val id: Long, val name: String) : IdItemModel<Long>
     *
     * val users = listOf(user1, user2)
     * users.removeDoubles(Equals.sameOrEq()) // Uses isSame() for comparison
     *
     * // For standard objects
     * val numbers = listOf(1, 2, 1, 3)
     * numbers.removeDoubles(Equals.sameOrEq()) // Uses == for comparison
     * ```
     *
     * @return composite predicate with fallthrough equality checks
     */
    fun <T1, T2> sameOrEq(): (T1, T2) -> Boolean = { it1, it2 ->
        when {
            it1 === it2 -> true
            it2 == null -> false
            else -> ((it1 as? SameModel)?.isSame(it2) ?: false) || it1 == it2
        }
    }

}
