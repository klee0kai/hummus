package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.IdItemModel
import kotlin.reflect.KClass

/**
 * Factory object for creating grouping key extraction functions.
 *
 * Provides standard key extraction strategies for grouping operations.
 * Each function extracts a different aspect of the item to use as the grouping key.
 *
 * **Typical usage with the group() extension:**
 * ```kotlin
 * people.group(
 *     groupId = GroupKeys.idKey(),
 *     grouping = GroupGrouping.groupingToGroups()
 * )
 * ```
 *
 * @see com.github.klee0kai.hummus.collections.ListExts.group
 * @see GroupGrouping for grouping transformation strategies
 */
object GroupKeys {

    /**
     * Groups items by their runtime class/type.
     *
     * Returns the KClass of the item, allowing grouping by type hierarchy.
     * Useful for polymorphic collections.
     *
     * **Usage:**
     * ```kotlin
     * sealed class Animal
     * class Dog : Animal()
     * class Cat : Animal()
     *
     * val animals: List<Animal> = listOf(Dog(), Cat(), Dog(), Cat())
     *
     * val byType = animals.group(
     *     groupId = GroupKeys.typeKey(),
     *     grouping = GroupGrouping.groupingToGroups()
     * )
     * // Separates Dogs and Cats into different groups
     * ```
     *
     * @return function that extracts the runtime class of an item
     */
    fun <Type : Any> typeKey(): (Type) -> KClass<out Type> = { it ->
        it::class
    }

    /**
     * Groups [IdItemModel] items by their ID.
     *
     * Extracts the ID field from objects implementing [IdItemModel],
     * enabling grouping by unique identifier.
     *
     * **Usage:**
     * ```kotlin
     * data class User(override val id: Long, val name: String) : IdItemModel<Long>
     *
     * val users = listOf(
     *     User(1, "Alice"),
     *     User(1, "Alice Updated"),
     *     User(2, "Bob")
     * )
     *
     * val groupedById = users.group(
     *     groupId = GroupKeys.idKey(),
     *     grouping = GroupGrouping.groupingToGroups()
     * )
     * // Result: [Group(1, [Alice, Alice Updated]), Group(2, [Bob])]
     * ```
     *
     * @param Type must implement [IdItemModel] with an ID property
     * @param IdType the type of the ID field
     * @return function that extracts the ID from an item
     */
    fun <Type : IdItemModel<IdType>, IdType> idKey(): (Type) -> IdType = { it ->
        it.id
    }

}
