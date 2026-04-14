package com.github.klee0kai.crossbox.processor.ksp.arch

import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * Container for symbols that need to be processed.
 *
 * This class separates symbols into two categories:
 * - **symbolsForProcessing**: symbols that should be processed in the current pass
 * - **symbolsForReprocessing**: symbols that were not processed and should be deferred to the next pass
 *
 * The [processOnlyTogether] flag is used to indicate that all symbols must be processed together
 * in a single pass (used for batch processing requirements).
 *
 * **Usage example:**
 *
 * Separating valid from invalid symbols during annotation processing:
 *
 * ```kotlin
 * override suspend fun findSymbolsToProcess(resolver: Resolver): SymbolsToProcess {
 *     val annotatedSymbols = resolver
 *         .getSymbolsWithAnnotation(GenClassInfo::class.qualifiedName!!)
 *         .groupBy { it.validate() } // validate() checks if all dependencies are resolvable
 *
 *     return SymbolsToProcess(
 *         symbolsForProcessing = annotatedSymbols[true].orEmpty(), // Valid, ready to process
 *         symbolsForReprocessing = annotatedSymbols[false].orEmpty(), // Invalid, will retry next pass
 *     )
 * }
 * ```
 *
 * @property symbolsForProcessing symbols to process in the current pass
 * @property symbolsForReprocessing symbols to defer to the next pass
 * @property processOnlyTogether if true, all symbols must be processed in one pass (prevents splitting)
 */
data class SymbolsToProcess(
    val symbolsForProcessing: List<KSAnnotated>,
    val symbolsForReprocessing: List<KSAnnotated>,
    val processOnlyTogether: Boolean = false,
)

/**
 * Limits the number of symbols processed in the current pass.
 *
 * Takes only the specified number of symbols from [symbolsForProcessing] and moves the rest
 * to [symbolsForReprocessing]. Respects [processOnlyTogether] flag unless [force] is true.
 *
 * Used for incremental build support to process symbols in batches.
 *
 * @param takeSymbolsCount maximum number of symbols to take
 * @param force if true, ignores [processOnlyTogether] flag
 * @return new [SymbolsToProcess] with split symbols
 */
fun SymbolsToProcess.nowTakeOnly(
    takeSymbolsCount: Int,
    force: Boolean = false,
): SymbolsToProcess {
    return if (!force && processOnlyTogether) {
        this
    } else {
        copy(
            symbolsForProcessing = symbolsForProcessing.take(takeSymbolsCount),
            symbolsForReprocessing = symbolsForReprocessing + symbolsForProcessing.drop(takeSymbolsCount),
        )
    }
}

/**
 * Filters symbols based on a predicate.
 *
 * Applies the filter to both [symbolsForProcessing] and [symbolsForReprocessing].
 * Respects [processOnlyTogether] flag unless [force] is true.
 *
 * Useful for debug filtering or narrowing down which symbols to process.
 *
 * @param force if true, ignores [processOnlyTogether] flag
 * @param filter predicate to apply to symbols
 * @return new [SymbolsToProcess] with filtered symbols
 */
fun SymbolsToProcess.filter(
    force: Boolean = false,
    filter: (KSAnnotated) -> Boolean,
): SymbolsToProcess {
    return if (!force && processOnlyTogether) {
        this
    } else {
        copy(
            symbolsForProcessing = symbolsForProcessing.filter(filter),
            symbolsForReprocessing = symbolsForReprocessing.filter(filter),
        )
    }
}

/**
 * Forces symbols matching the filter to be processed in the current pass.
 *
 * Moves symbols from [symbolsForReprocessing] back to [symbolsForProcessing] if they match the filter.
 * This is used internally by [TargetKSPProcessor] to prioritize reprocessing of certain symbols.
 *
 * @param filter predicate to match symbols to force process (default: matches nothing)
 * @return new [SymbolsToProcess] with reclassified symbols
 */
fun SymbolsToProcess.forceProcess(
    filter: (KSAnnotated) -> Boolean = { false },
): SymbolsToProcess {
    val symbolsForProcessing = (symbolsForProcessing + symbolsForReprocessing.filter(filter)).toSet()
    return copy(
        symbolsForProcessing = symbolsForProcessing.toList(),
        symbolsForReprocessing = symbolsForReprocessing.filter { it !in symbolsForProcessing }
    )
}