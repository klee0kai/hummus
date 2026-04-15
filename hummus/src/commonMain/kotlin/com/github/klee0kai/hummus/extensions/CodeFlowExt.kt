package com.github.klee0kai.hummus.extensions

/**
 * Conditionally applies a transformation to an object.
 *
 * Executes the [block] only if [condition] is true, returning the transformed object.
 * Otherwise returns the original object unchanged. Useful for conditional method chaining.
 *
 * **Use cases:**
 * - Conditional object configuration
 * - Builder pattern with optional steps
 * - Fluent API with conditional logic
 *
 * **Usage examples:**
 *
 * Configuring objects conditionally:
 * ```kotlin
 * data class Config(var name: String = "", var debug: Boolean = false)
 *
 * val config = Config()
 *     .then(isProduction == false) {
 *         apply { debug = true }
 *     }
 *     .then(environment == "staging") {
 *         apply { name = "staging-server" }
 *     }
 * ```
 *
 * Building UI conditionally:
 * ```kotlin
 * Button(text = "OK")
 *     .then(isEnabled) {
 *         setEnabled(true)
 *         setClickListener { ... }
 *     }
 *     .then(isDarkMode) {
 *         setBackgroundColor(Color.BLACK)
 *         setTextColor(Color.WHITE)
 *     }
 * ```
 *
 * String building:
 * ```kotlin
 * val message = "Hello"
 *     .then(includeTime) { "$this at ${LocalTime.now()}" }
 *     .then(addName != null) { "$this, $addName" }
 * ```
 *
 * **vs. alternatives:**
 * - If/else: `if (condition) obj.apply { ... } else obj` (verbose)
 * - `also` with if: doesn't work well with method chaining
 * - `then`: clean, fluent, reads naturally
 *
 * **Type parameter:**
 * Type is inferred from receiver, so you don't need explicit types.
 *
 * @param T the type of object being transformed
 * @param condition whether to apply the transformation
 * @param block transformation function (extension of T that returns T)
 * @return transformed object if condition is true, original object otherwise
 */
inline fun <reified T> T.then(
    condition: Boolean,
    block: T.() -> T,
) = if (condition) {
    block()
} else {
    this
}
