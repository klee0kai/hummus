package com.github.klee0kai.hummus.model

/**
 * Interface for objects that have a unique identifier.
 *
 * Provides default [isSame] implementation that compares objects by:
 * 1. Type equality (same class)
 * 2. ID equality
 *
 * **Usage example:**
 * ```kotlin
 * data class User(
 *     override val id: Long,
 *     val name: String,
 *     val email: String,
 * ) : IdItemModel<Long>
 *
 * val user1 = User(id = 1, name = "Alice", email = "alice@example.com")
 * val user2 = User(id = 1, name = "Alice Updated", email = "alice@updated.com")
 *
 * user1.isSame(user2) // true - same ID
 * user1 == user2      // false - different email
 * ```
 *
 * Useful for:
 * - Domain entities with natural identifiers
 * - Deduplicating collections by ID
 * - Entity tracking in repositories
 *
 * @param IdType the type of the unique identifier
 */
interface IdItemModel<IdType> : SameModel {

    /**
     * The unique identifier for this item.
     */
    val id: IdType

    /**
     * Compares this item with another by class and ID.
     *
     * Returns true only if:
     * - Both objects are of the same class
     * - Both have the same ID value
     *
     * @param o the object to compare with
     * @return true if both objects are the same type and have the same ID
     */
    override fun isSame(o: Any): Boolean {
        return this::class == o::class
                && id == (o as? IdItemModel<IdType>)?.id
    }

}