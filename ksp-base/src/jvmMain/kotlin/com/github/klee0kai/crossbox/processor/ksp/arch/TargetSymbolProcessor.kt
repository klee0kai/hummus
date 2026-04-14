package com.github.klee0kai.crossbox.processor.ksp.arch

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

interface TargetSymbolProcessor {

    suspend fun findSymbolsToProcess(
        resolver: Resolver,
    ): SymbolsToProcess

    suspend fun process(
        targetSymbol: KSAnnotated,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger,
    ): GenSpec?


    suspend fun multiSymbolsProcess(
        targetSymbol: List<KSAnnotated>,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger,
    ): GenSpec? = null

}