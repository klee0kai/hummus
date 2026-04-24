package com.github.klee0kai.crossbox.processor.exceptions

import com.google.devtools.ksp.symbol.KSNode


/**
 * Base exception for KSP processing errors.
 *
 * This exception extends [IllegalStateException] and adds support for tracking the KSP symbol
 * ([KSNode]) that caused the error. This is useful for providing accurate error locations
 * and context when processing fails.
 *
 * The exception can have a nested cause exception, and [findErrorElement] will recursively
 * search through the cause chain to find the original error element.
 *
 * **Usage example:**
 * ```kotlin
 * try {
 *     processSymbol(symbol)
 * } catch (e: Exception) {
 *     throw KspBaseException(
 *         message = "Failed to process symbol: ${symbol.simpleName.asString()}",
 *         cause = e,
 *         element = symbol
 *     )
 * }
 * ```
 *
 * @property element the KSP node ([KSNode]) that caused this error, if available
 * @throws IllegalStateException this exception extends IllegalStateException for standard error handling
 */
open class KspBaseException(
    message: String? = null,
    cause: Throwable? = null,
    val element: KSNode? = null,
) : IllegalStateException(message, cause) {

    /**
     * Finds the original error element by searching through the exception cause chain.
     *
     * If this exception has a [KspBaseException] cause, recursively searches its cause chain
     * to find the original error element. Falls back to this exception's [element] if
     * no error element is found in the cause chain.
     *
     * @return the original [KSNode] that caused the error, or null if not found
     */
    fun findErrorElement(): KSNode? {
        var sourceElement: KSNode? = null
        if (cause is KspBaseException) {
            sourceElement = (cause as KspBaseException).findErrorElement()
        }
        if (sourceElement == null) sourceElement = element
        return sourceElement
    }

}
