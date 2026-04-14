package com.github.klee0kai.crossbox.processor.ksp.arch

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * Interface for implementing custom KSP symbol processors.
 *
 * This is the main interface for extending framework functionality. Each processor is responsible for:
 * 1. Finding symbols that require processing
 * 2. Processing discovered symbols and generating code
 * 3. Optionally - processing multiple symbols together
 *
 * **Real-world example:**
 *
 * This example generates extension properties that provide metadata about annotated classes:
 *
 * ```kotlin
 * @Retention(AnnotationRetention.SOURCE)
 * annotation class GenClassInfo
 *
 * class ClassInfoTargetProcessor : TargetSymbolProcessor {
 *
 *     override suspend fun findSymbolsToProcess(resolver: Resolver): SymbolsToProcess {
 *         // Find all classes annotated with @GenClassInfo
 *         val annotatedSymbols = resolver
 *             .getSymbolsWithAnnotation(GenClassInfo::class.qualifiedName!!)
 *             .groupBy { it.validate() } // Separate valid from invalid symbols
 *
 *         return SymbolsToProcess(
 *             symbolsForProcessing = annotatedSymbols[true].orEmpty(),
 *             symbolsForReprocessing = annotatedSymbols[false].orEmpty(), // Will retry later
 *         )
 *     }
 *
 *     override suspend fun process(
 *         targetSymbol: KSAnnotated,
 *         resolver: Resolver,
 *         options: Map<String, String>,
 *         logger: KSPLogger,
 *     ): GenSpec? {
 *         val fileOwner = targetSymbol.containingFile ?: return null
 *         val classDecl = targetSymbol as? KSClassDeclaration ?: return null
 *
 *         // Generate extension property: classDecl.classInfo
 *         val fileSpec = genFileSpec(
 *             packageName = classDecl.packageName.asString(),
 *             fileName = classDecl.simpleName.asString() + "Ext",
 *         ) {
 *             genProperty(
 *                 name = "classInfo",
 *                 type = ClassInfo::class.asClassName(),
 *             ) {
 *                 receiver(classDecl.toClassName())
 *                 genGetter {
 *                     addCode(
 *                         "return %T(packageName = %S, simpleName = %S)",
 *                         ClassInfo::class.asClassName(),
 *                         classDecl.packageName.asString(),
 *                         classDecl.simpleName.asString(),
 *                     )
 *                 }
 *             }
 *         }
 *
 *         return GenSpec(
 *             fileSpec = fileSpec,
 *             dependencies = Dependencies(aggregating = false, fileOwner),
 *         )
 *     }
 * }
 * ```
 *
 * **Key points:**
 * - Use [validate()] to separate symbols that are ready to process from those with unresolved dependencies
 * - Return [GenSpec] with proper [Dependencies] for correct incremental builds
 * - Use DSL functions (genFileSpec, genProperty, etc.) for type-safe code generation
 */
interface TargetSymbolProcessor {

    /**
     * Finds symbols that require processing.
     *
     * This method is called at the beginning of each processing pass and should return
     * all symbols that require processing or reprocessing.
     *
     * @param resolver KSP resolver for finding symbols
     * @return [SymbolsToProcess] with symbols for processing and reprocessing
     */
    suspend fun findSymbolsToProcess(
        resolver: Resolver,
    ): SymbolsToProcess

    /**
     * Processes a single symbol and generates code for it.
     *
     * Called for each symbol from [findSymbolsToProcess] separately.
     *
     * @param targetSymbol symbol to process
     * @param resolver KSP resolver for working with symbols
     * @param options configuration options from gradle/maven
     * @param logger logger for output messages
     * @return [GenSpec] with generated code or null if no code generation is needed
     */
    suspend fun process(
        targetSymbol: KSAnnotated,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger,
    ): GenSpec?

    /**
     * Processes multiple symbols together.
     *
     * Called once with all discovered symbols. Useful for operations that require
     * analyzing multiple symbols simultaneously (e.g., generating a single registry or index).
     *
     * Default implementation returns null (batch processing is not required).
     *
     * @param targetSymbol list of symbols to process
     * @param resolver KSP resolver for working with symbols
     * @param options configuration options from gradle/maven
     * @param logger logger for output messages
     * @return [GenSpec] with generated code or null if batch processing is not required
     */
    suspend fun multiSymbolsProcess(
        targetSymbol: List<KSAnnotated>,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger,
    ): GenSpec? = null

}