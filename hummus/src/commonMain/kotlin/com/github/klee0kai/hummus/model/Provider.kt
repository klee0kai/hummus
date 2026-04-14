package com.github.klee0kai.hummus.model

import kotlin.reflect.KProperty

/**
 * Functional interface for providing values on demand.
 *
 * A simple abstraction for lazy value provision, useful for dependency injection,
 * lazy initialization, and functional programming patterns.
 *
 * **Usage examples:**
 *
 * Basic value provision:
 * ```kotlin
 * val stringProvider: Provider<String> = Provider { "Hello" }
 * val value = stringProvider.get() // Returns "Hello"
 * ```
 *
 * As a property delegate:
 * ```kotlin
 * class MyClass {
 *     val config: Config by configProvider // Uses getValue operator
 * }
 *
 * val configProvider = Provider { loadConfig() }
 * ```
 *
 * Inline scope function:
 * ```kotlin
 * provider {
 *     println(this) // 'this' is the provided value
 *     result
 * }
 * ```
 *
 * @param T the type of value to provide
 */
fun interface Provider<T> {

    /**
     * Provides the value.
     *
     * @return the provided value
     */
    fun get(): T

}

/**
 * Enables using [Provider] as a property delegate.
 *
 * Allows Provider to be used with Kotlin's delegation syntax:
 *
 * ```kotlin
 * class MyClass {
 *     val database: Database by databaseProvider
 * }
 * ```
 *
 * @return the provided value, or null if get() returns null
 */
operator fun <T> Provider<T>.getValue(t: Any?, property: KProperty<*>): T? = get()

/**
 * Enables calling [Provider] as an inline scope function.
 *
 * Provides the value and executes a block in the context of that value:
 *
 * ```kotlin
 * val result = configProvider { size = 100; name = "test" }
 *
 * // Similar to:
 * val config = configProvider.get()
 * val result = config.run { size = 100; name = "test" }
 * ```
 *
 * @param body lambda to execute with the provided value as receiver
 * @return the result of executing the body
 */
inline operator fun <T, R> Provider<T>.invoke(
    crossinline body: T.() -> R,
): R {
    return get().run(body)
}


