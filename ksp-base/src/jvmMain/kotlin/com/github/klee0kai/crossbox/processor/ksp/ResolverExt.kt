package com.github.klee0kai.crossbox.processor.ksp

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

/**
 * Checks if this function is a suspend function.
 *
 * Suspend functions can be paused and resumed and are used with coroutines.
 * They can only be called from within a coroutine scope or from another suspend function.
 *
 * **Usage example:**
 * ```kotlin
 * val function: KSFunctionDeclaration = ...
 * if (function.isSuspend) {
 *     println("This is a suspend function")
 * }
 * ```
 */
val KSFunctionDeclaration.isSuspend: Boolean get() = modifiers.contains(Modifier.SUSPEND)