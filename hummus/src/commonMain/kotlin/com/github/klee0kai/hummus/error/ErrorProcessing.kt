/**
 * Utilities for analyzing exception cause chains.
 *
 * Kotlin/Java exceptions can have nested causes. These utilities help traverse
 * the cause chain to find specific exception types, useful for understanding
 * the root cause of failures in nested operations.
 */
package com.github.klee0kai.hummus.error

import kotlin.reflect.KClass

/**
 * Generates a sequence of all exceptions in the cause chain.
 *
 * Starts with this exception and follows the [Throwable.cause] chain to produce
 * all exceptions in the hierarchy. The chain continues until cause is null.
 *
 * **Example cause chain:**
 * ```
 * IOException
 *  └─ cause: SocketException
 *      └─ cause: ConnectException
 *          └─ cause: null
 * ```
 *
 * **Usage:**
 * ```kotlin
 * val exception = IOException("Failed to connect")
 * exception.causes().forEach { cause ->
 *     println(cause::class.simpleName)  // IOException, SocketException, ...
 * }
 * ```
 *
 * **Lazy evaluation:**
 * Returns a [Sequence], so traversal is lazy. Can stop early or iterate selectively.
 *
 * **Use cases:**
 * - Finding root cause of wrapped exceptions
 * - Checking for specific exception types in chain
 * - Logging entire exception hierarchy
 * - Extracting information from nested exceptions
 *
 * @return sequence of exceptions from this through cause chain
 *
 * @see cause for finding specific exception types
 * @see isCause for checking if exception is in chain
 */
fun Throwable.causes() = generateSequence(this) { it.cause }

/**
 * Finds the first exception of the specified type in the cause chain.
 *
 * Traverses the exception cause chain looking for an exception of type [T].
 * Useful for extracting specific exceptions from nested/wrapped exceptions.
 *
 * **Usage example:**
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: Exception) {
 *     val ioError = e.cause<IOException>()
 *     if (ioError != null) {
 *         handleIOError(ioError)
 *     }
 * }
 * ```
 *
 * **vs. [cause(KClass)]:**
 * - [cause] with reified T: simpler syntax, requires type reification
 * - [cause(KClass)]: explicit KClass, works without reification
 *
 * **Return value:**
 * Returns the first matching exception or null if not found in chain.
 *
 * @param T the type of exception to find
 * @return first exception of type T in chain, or null if not found
 *
 * @see causes for iterating all exceptions
 * @see isCause for checking without casting
 */
inline fun <reified T : Any> Throwable.cause(cl: KClass<T>) =
    causes().firstOrNull { cl.isInstance(it) } as? T

/**
 * Finds the first exception of the specified reified type in the cause chain.
 *
 * Traverses the exception cause chain looking for an exception matching the reified type.
 * The type parameter is inferred from context, so no explicit KClass is needed.
 *
 * **Syntax variants:**
 * ```kotlin
 * val ioError = exception.cause<IOException>()      // Reified type (recommended)
 * val ioError = exception.cause(IOException::class) // Explicit KClass
 * ```
 *
 * **Performance note:**
 * Stops at first match, doesn't traverse entire chain unless exception not found.
 *
 * **Example:**
 * ```kotlin
 * try {
 *     database.query()
 * } catch (e: Exception) {
 *     e.cause<SQLException>()?.let { sql ->
 *         log("SQL Error: ${sql.message}")
 *     }
 * }
 * ```
 *
 * @param T the exception type to find (reified at compile time)
 * @return first exception of type T in chain, or null
 *
 * @see cause for KClass-based version
 */
inline fun <reified T> Throwable.cause() =
    causes().firstOrNull { T::class.isInstance(it) } as? T

/**
 * Checks if the exception type is present in the cause chain.
 *
 * Returns true if an exception of type [T] exists anywhere in the cause chain.
 * Equivalent to `cause(cl) != null` but with clearer intent.
 *
 * **Use cases:**
 * - Checking exception type without extracting it
 * - Deciding error handling strategy
 * - Conditional error recovery logic
 *
 * **Example:**
 * ```kotlin
 * try {
 *     operation()
 * } catch (e: Exception) {
 *     if (e.isCause<TimeoutException>()) {
 *         retryWithBackoff()
 *     } else if (e.isCause<PermissionException>()) {
 *         requestPermission()
 *     }
 * }
 * ```
 *
 * @param T the exception type to check for
 * @param cl the KClass of the exception type
 * @return true if exception of type T is in cause chain, false otherwise
 *
 * @see cause for extracting the exception
 */
inline fun <reified T : Any> Throwable.isCause(cl: KClass<T>) = cause(cl) != null

