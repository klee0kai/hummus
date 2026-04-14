package com.github.klee0kai.crossbox.processor.ksp.arch

import com.google.devtools.ksp.symbol.KSAnnotated

data class SymbolsToProcess(
    val symbolsForProcessing: List<KSAnnotated>,
    val symbolsForReprocessing: List<KSAnnotated>,
    val processOnlyTogether: Boolean = false,
)


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

fun SymbolsToProcess.forceProcess(
    filter: (KSAnnotated) -> Boolean = { false },
): SymbolsToProcess {
    val symbolsForProcessing = (symbolsForProcessing + symbolsForReprocessing.filter(filter)).toSet()
    return copy(
        symbolsForProcessing = symbolsForProcessing.toList(),
        symbolsForReprocessing = symbolsForReprocessing.filter { it !in symbolsForProcessing }
    )
}