/**
 * Collection building and transformation utilities.
 */
package com.github.klee0kai.hummus.collections.ext

/**
 * Builds a list by calling an action N times.
 *
 * Repeats an action [count] times, collecting results into a list.
 * Similar to `(0 until count).map { action() }` but more efficient.
 *
 * **Use cases:**
 * - Creating lists of identical/related objects
 * - Building test fixtures with repeated items
 * - Generating sequences of computed values
 * - Pre-allocating collections
 *
 * **Example:**
 * ```kotlin
 * // Create 5 default User objects
 * val users = buildListCount(5) { User("User${it}") }
 * // Result: [User0, User1, User2, User3, User4]
 *
 * // Create list of counters
 * val counters = buildListCount(3) { Counter() }
 * // Result: [Counter(), Counter(), Counter()]
 * ```
 *
 * **vs. kotlin.repeat:**
 * - [buildListCount]: collects results into list
 * - [repeat]: executes for side effects, returns Unit
 *
 * **vs. map on range:**
 * - [buildListCount]: clearer intent, doesn't expose index (if not needed)
 * - `(0 until count).map { ... }`: index available in lambda
 * - [buildListCount]: potentially more efficient
 *
 * **Performance note:**
 * Creates list with capacity = count for minimal reallocations.
 *
 * @param T the type of elements to build
 * @param count number of times to call [action]
 * @param action function that creates one element (called [count] times)
 * @return list with [count] elements produced by [action]
 */
inline fun <reified T> buildListCount(count: Int, action: () -> T): List<T> {
    val list = mutableListOf<T>()
    repeat(count) {
        list.add(action())
    }
    return list
}

/**
 * Iterates over items calling an extension function on each.
 *
 * Similar to [forEach] but the action is an extension function, allowing
 * cleaner syntax when you want to call methods on items as receivers.
 *
 * **Syntax comparison:**
 * ```kotlin
 * val items = listOf(obj1, obj2, obj3)
 *
 * // Standard forEach
 * items.forEach { item -> item.process() }
 *
 * // Using runForEach
 * items.runForEach { process() }  // this is the item
 * ```
 *
 * **Use cases:**
 * - Calling multiple methods on items
 * - Performing side effects (logging, updating state)
 * - Configuration/initialization loops
 * - Batch operations on items
 *
 * **Example:**
 * ```kotlin
 * class Widget {
 *     fun activate() = println("Activated")
 *     fun configure() = println("Configured")
 * }
 *
 * val widgets = listOf(Widget(), Widget(), Widget())
 *
 * // Compact syntax with receiver
 * widgets.runForEach {
 *     configure()
 *     activate()
 * }
 * ```
 *
 * **vs. forEach:**
 * - [runForEach]: item is receiver (this), cleaner for multiple calls
 * - [forEach]: item is parameter, explicit declaration
 *
 * **vs. apply:**
 * - [apply]: returns the item, used for initialization
 * - [runForEach]: doesn't return, used for side effects
 *
 * @param T element type
 * @param action extension function to call on each item
 */
inline fun <T> Iterable<T>.runForEach(action: T.() -> Unit) =
    forEach { action.invoke(it) }

/**
 * Accumulates values by applying a function to consecutive pairs.
 *
 * Similar to [fold] but uses the first element as the initial accumulator.
 * Iterates through items, applying the action to the accumulated result and current item.
 *
 * **Accumulation steps:**
 * Given input: [1, 2, 3, 4]
 * With action: (a, b) -> a + b
 * ```
 * Step 1: result = 1           (first element)
 * Step 2: result = 1 + 2 = 3   (action(1, 2))
 * Step 3: result = 3 + 3 = 6   (action(3, 3))
 * Step 4: result = 6 + 4 = 10  (action(6, 4))
 * Final: 10
 * ```
 *
 * **Use cases:**
 * - Computing running sums, products, or other aggregates
 * - Chaining transformations through a sequence
 * - Combining pairs of values progressively
 * - Implementing custom reduce-like operations
 *
 * **Examples:**
 * ```kotlin
 * // Sum of numbers
 * listOf(1, 2, 3, 4).accumulate { a, b -> a + b }  // 10
 *
 * // Product
 * listOf(2, 3, 4).accumulate { a, b -> a * b }     // 24
 *
 * // String concatenation
 * listOf("a", "b", "c").accumulate { a, b -> "$a-$b" } // "a-b-c"
 *
 * // Custom class combination
 * data class Point(val x: Int, val y: Int) {
 *     operator fun plus(other: Point) = Point(x + other.x, y + other.y)
 * }
 * listOf(Point(1,1), Point(2,2), Point(3,3))
 *     .accumulate { a, b -> a + b }  // Point(6, 6)
 * ```
 *
 * **vs. fold:**
 * - [accumulate]: first element is initial value
 * - [fold]: explicit initial value provided
 * - [accumulate]: simpler when you want first element as start
 * - [fold]: more explicit, can use any initial value
 *
 * **vs. reduce:**
 * - [accumulate]: same as reduce for non-empty iterables
 * - [reduce]: throws on empty iterable
 * - [accumulate]: returns null for empty iterable
 *
 * **Return value:**
 * - Non-empty iterable: final accumulated value
 * - Empty iterable: null
 *
 * @param T element type (inferred)
 * @param action function combining accumulated value with current item
 * @return accumulated result, or null if iterable is empty
 *
 * @see fold for explicit initial value
 * @see reduce for throwing on empty
 */
inline fun <reified T> Iterable<T>.accumulate(action: (T, T) -> T): T? {
    var last: T? = null
    forEachIndexed { index, t ->
        last = if (index > 0) {
            action(last as T, t)
        } else {
            t
        }
    }
    return last
}

/**
 * Generates all combinations of elements from multiple dimensions.
 *
 * Creates the Cartesian product of multiple lists - every possible combination
 * of one element from each dimension. Similar to nested loops but generated lazily.
 *
 * **Concept (Cartesian product):**
 * Given dimensions: [A, B] × [1, 2] × [X, Y]
 * Produces: [A,1,X], [A,1,Y], [A,2,X], [A,2,Y], [B,1,X], [B,1,Y], [B,2,X], [B,2,Y]
 *
 * **Size calculation:**
 * Total combinations = product of all dimension sizes
 * For [2, 3, 2] = 2 × 3 × 2 = 12 combinations
 *
 * **Use cases:**
 * - Testing all parameter combinations (combinatorial testing)
 * - Generating configuration variants
 * - Building complete option matrices
 * - Enumerating all possible states
 *
 * **Examples:**
 *
 * Simple combination:
 * ```kotlin
 * val variants = enumerateAllVariants(
 *     listOf("A", "B"),
 *     listOf(1, 2, 3)
 * ).toList()
 * // Result: [[A,1], [A,2], [A,3], [B,1], [B,2], [B,3]]
 * ```
 *
 * Three dimensions:
 * ```kotlin
 * val colors = listOf("Red", "Green", "Blue")
 * val sizes = listOf("S", "M", "L")
 * val materials = listOf("Cotton", "Wool")
 *
 * val products = enumerateAllVariants(colors, sizes, materials)
 * // Generates all 3×3×2 = 18 product variants
 * ```
 *
 * Combinatorial testing:
 * ```kotlin
 * val testCases = enumerateAllVariants(
 *     listOf(true, false),           // debug mode
 *     listOf("en", "fr", "de"),      // language
 *     listOf("light", "dark")        // theme
 * )
 * // Tests all 2×3×2 = 12 combinations
 * ```
 *
 * **Lazy evaluation:**
 * Returns a [Sequence], so combinations are generated on-demand.
 * Can iterate partially or use operators like [take], [filter].
 *
 * **Memory usage:**
 * - Memory: O(N) where N is number of dimensions (not combinations!)
 * - Perfect for large result sets (millions of combinations)
 *
 * **Algorithm:**
 * Uses multi-dimensional index counter (like speedometer):
 * - Rightmost dimension increments fastest
 * - Carry over to next dimension when one completes
 * - Stops when all dimensions are exhausted
 *
 * **Edge cases:**
 * - Empty dimension: produces no variants (stops immediately)
 * - Single dimension: produces all elements as single-item lists
 * - Multiple empty lists: first empty dimension terminates sequence
 *
 * @param dimensionVariants variable number of lists representing each dimension
 * @return lazy sequence of all possible combinations
 */
fun enumerateAllVariants(
    vararg dimensionVariants: List<Any?>,
) = sequence<List<Any?>> {
    val dimIdxs = buildListCount(dimensionVariants.size) { 0 }.toMutableList()
    if (dimensionVariants.any { it.isEmpty() }) return@sequence
    while (true) {
        yield(
            buildList(dimensionVariants.size) {
                repeat(dimensionVariants.size) { idx ->
                    add(dimensionVariants[idx][dimIdxs[idx]])
                }
            }
        )

        for (i in dimensionVariants.indices) {
            if (dimIdxs[i] < dimensionVariants[i].size - 1) {
                dimIdxs[i]++
                break
            } else {
                dimIdxs[i] = 0
            }
        }
        if (dimIdxs.all { it == 0 }) break
    }
}