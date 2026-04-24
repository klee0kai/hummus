package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.stone.weakref.Ref
import com.github.klee0kai.stone.weakref.SoftRef

/**
 * A [RefList] that stores elements as soft references.
 *
 * Elements are only garbage collected when memory is low. This provides a middle ground
 * between strong and weak references, suitable for caching scenarios.
 *
 * **Use cases:**
 * - Caching of expensive-to-create objects
 * - Intermediate results that can be recreated if needed
 * - Memory-aware collections that survive until memory pressure increases
 *
 * **Example - Image cache:**
 * ```kotlin
 * class ImageCache {
 *     private val cache = SoftList<Bitmap>()
 *
 *     fun getCachedImage(id: String): Bitmap? {
 *         cache.clearNulls() // Remove GC'd images
 *         return cache.find { it?.id == id }
 *     }
 *
 *     fun cacheImage(bitmap: Bitmap) {
 *         cache.add(bitmap)
 *     }
 * }
 * ```
 *
 * **Memory behavior:**
 * - Objects are kept alive as long as memory is available
 * - Garbage collected only when JVM/system memory is low
 * - Softer than weak references, allowing longer-term caching
 * - Best for objects that are expensive to recreate
 *
 * **Comparison with WeakList:**
 * - WeakList: immediate collection when no other references exist
 * - SoftList: kept alive until memory pressure forces collection
 *
 * @param T the element type
 *
 * @see WeakList for more aggressive collection policy
 * @see RefList for base class with full documentation
 */
open class SoftList<T>() : RefList<T>() {

    /**
     * Creates a SoftList with initial elements.
     *
     * @param list initial elements to add to the list
     */
    constructor(list: Iterable<T?>) : this() {
        addAll(list)
    }

    /**
     * Wraps elements in soft references.
     */
    override fun wrapRef(it: T?): Ref<T?> = SoftRef(it)

    /**
     * Creates a new SoftList with the given elements.
     */
    override fun createNew(list: List<T?>): RefList<T> = SoftList(list)
}
