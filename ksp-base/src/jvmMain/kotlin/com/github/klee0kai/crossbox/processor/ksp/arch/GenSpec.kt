package com.github.klee0kai.crossbox.processor.ksp.arch

import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec

/**
 * Specification for generated code and its dependencies.
 *
 * This data class wraps the KotlinPoet [FileSpec] (representing the generated code)
 * together with KSP [Dependencies] that track which symbols were used to generate this file.
 *
 * The dependencies are crucial for KSP's incremental compilation - they allow KSP to
 * determine which generated files need to be regenerated when their source symbols change.
 *
 * **Real-world example:**
 *
 * Creating GenSpec after processing an annotated class:
 *
 * ```kotlin
 * val classDecl: KSClassDeclaration = ...
 * val fileOwner = classDecl.containingFile ?: return null
 *
 * val fileSpec = genFileSpec(...) { ... }
 *
 * return GenSpec(
 *     fileSpec = fileSpec,
 *     // Mark this generated file as dependent on classDecl's source file
 *     // aggregating=false means it depends only on this specific source file
 *     dependencies = Dependencies(aggregating = false, fileOwner),
 * )
 * ```
 *
 * **Dependencies types:**
 * - `Dependencies(aggregating = false, fileOwner)` - depends only on specific source files (non-aggregating)
 * - `Dependencies(aggregating = true, ...)` - depends on any symbol matching a pattern (aggregating, slower rebuilds)
 *
 * @property fileSpec the KotlinPoet FileSpec representing the generated Kotlin code
 * @property dependencies the KSP Dependencies tracking which symbols generated this file
 *
 * @see FileSpec
 * @see Dependencies
 */
data class GenSpec(
    val fileSpec: FileSpec,
    val dependencies: Dependencies,
)
