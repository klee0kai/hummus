package com.github.klee0kai.crossbox.processor

import com.github.klee0kai.crossbox.processor.exceptions.KspBaseException
import com.github.klee0kai.crossbox.processor.ksp.arch.*
import com.github.klee0kai.hummus.coroutine.LaunchConductor
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

/**
 * Main KSP processor that manages the execution of symbol processing.
 *
 * This class coordinates the work of an array of [TargetSymbolProcessor]s and processes discovered symbols
 * in parallel or sequential mode depending on configuration.
 *
 * **Processing workflow:**
 * 1. Each [TargetSymbolProcessor] finds symbols to process via [TargetSymbolProcessor.findSymbolsToProcess]
 * 2. Symbols are processed in batches of size [oneRunSymbolsCount] to support incremental builds
 * 3. For each symbol, [TargetSymbolProcessor.process] and/or [TargetSymbolProcessor.multiSymbolsProcess] is called
 * 4. Generated specifications ([GenSpec]) are written to code via [CodeGenerator]
 * 5. Unprocessed symbols are returned for reprocessing in the next pass
 *
 * **Configuration options:**
 * - `oneRunSymbolsCount`: maximum number of symbols to process in one pass (default = number of processors)
 * - `multithread`: enable multithreading (default false)
 * - `debug`: enable debug mode
 * - `debugPkgFilter`: package filter for debugging (processes only symbols from specified package)
 *
 * **gradle configuration example:**
 * ```gradle
 * ksp {
 *     arg("oneRunSymbolsCount", "10")
 *     arg("multithread", "true")
 *     arg("debug", "false")
 * }
 * ```
 *
 * **Processor registration:**
 * Create a `SymbolProcessorProvider` and return instances of `TargetKSPProcessor`:
 *
 * ```kotlin
 * class MySymbolProcessorProvider : SymbolProcessorProvider {
 *     override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
 *         return TargetKSPProcessor(
 *             targetProcessors = arrayOf(
 *                 ClassInfoTargetProcessor(),
 *                 AnotherProcessor(),
 *             ),
 *             options = environment.options,
 *             logger = environment.logger,
 *             codeGenerator = environment.codeGenerator,
 *         )
 *     }
 * }
 * ```
 *
 * @property targetProcessors array of symbol processors
 * @property options configuration options from gradle/maven
 * @property logger KSP logger
 * @property codeGenerator code generator for writing files
 * @property oneRunSymbolsCount max symbols per processing run for incremental builds
 * @property multithread enable multithreading
 * @property debug debug mode flag
 * @property debugPkgFilter package filter for debugging
 */
open class TargetKSPProcessor(
    val targetProcessors: Array<TargetSymbolProcessor>,
    val options: Map<String, String>,
    val logger: KSPLogger,
    val codeGenerator: CodeGenerator,
    val oneRunSymbolsCount: Int = options["oneRunSymbolsCount"]?.toInt()
        ?: max(Runtime.getRuntime().availableProcessors(), 4),

    val multithread: Boolean = options["multithread"]?.toBoolean() ?: false,
    val debug: Boolean = options["debug"]?.toBoolean() ?: false,
    var debugPkgFilter: String? = options["debugPkgFilter"],
) : SymbolProcessor {

    /**
     * Dispatcher for executing asynchronous operations.
     * Uses [Dispatchers.Default] for multithreading, otherwise [Dispatchers.Unconfined].
     */
    open val dispatcher by lazy { if (multithread) Dispatchers.Default else Dispatchers.Unconfined }

    /**
     * Processes discovered symbols and generates code.
     *
     * @param resolver resolver for working with KSP symbols
     * @return list of symbols for reprocessing in the next pass
     */
    override fun process(
        resolver: Resolver
    ): List<KSAnnotated> = runBlocking(dispatcher) {

        val processSymbolsCounter = AtomicInteger(0)
        val findSymbolsMutex = Mutex()
        val globalSymbolsForProcessing = ConcurrentLinkedQueue<KSAnnotated>()
        val globalSymbolsForReprocessing = ConcurrentLinkedQueue<KSAnnotated>()
        val genSpecs = ConcurrentLinkedQueue<GenSpec>()

        val launchConductor = LaunchConductor()

        val generateCodeJob = launch {
            targetProcessors.forEach { processor ->
                launch {
                    var symbols = launchConductor.finishTogether {
                        var symbols = findSymbolsMutex.withLock { processor.findSymbolsToProcess(resolver) }

                        if (!symbols.processOnlyTogether) {
                            // ---- skip to next run ( support incremental build ) -----
                            var takeSymbolsCount = 0
                            processSymbolsCounter.updateAndGet { totalCount ->
                                takeSymbolsCount = min(
                                    symbols.symbolsForProcessing.size,
                                    oneRunSymbolsCount - totalCount
                                )

                                takeSymbolsCount = max(takeSymbolsCount, 0)
                                totalCount + takeSymbolsCount
                            }
                            symbols = symbols.nowTakeOnly(takeSymbolsCount = takeSymbolsCount)

                        } else {
                            // ---- do not skip to next run ( incremental build is ignoring ) -----
                            processSymbolsCounter.updateAndGet { totalCount ->
                                totalCount + symbols.symbolsForProcessing.size
                            }
                        }

                        globalSymbolsForProcessing.addAll(symbols.symbolsForProcessing)

                        symbols
                    }

                    symbols = symbols.forceProcess { it in globalSymbolsForProcessing }

                    if (debug && debugPkgFilter != null) {
                        symbols = symbols.filter(force = debug) {
                            it.containingFile?.packageName?.asString()?.startsWith(debugPkgFilter!!) ?: true
                        }

                    }
                    globalSymbolsForReprocessing.addAll(symbols.symbolsForReprocessing)

                    if (symbols.symbolsForProcessing.isNotEmpty()) {
                        try {
                            processor.multiSymbolsProcess(
                                targetSymbols = symbols.symbolsForProcessing,
                                resolver = resolver,
                                options = options,
                                logger = logger,
                            )?.let {
                                genSpecs.add(it)
                            }

                            genSpecs.addAll(
                                symbols.symbolsForProcessing
                                    .mapNotNull { targetSymbol ->
                                        processor.process(
                                            targetSymbol = targetSymbol,
                                            resolver = resolver,
                                            options = options,
                                            logger = logger,
                                        )
                                    }
                            )
                        } catch (e: KspBaseException) {
                            logger.error(e.toString(), e.findErrorElement())
                        }
                    }
                }
            }
        }

        // join generate code
        // we provide separate file recording
        // with symbol resolution so that the processor can link the input and output of generation
        generateCodeJob.join()

        genSpecs.forEach { genSpec ->
            genSpec?.fileSpec?.writeTo(
                codeGenerator = codeGenerator,
                dependencies = genSpec.dependencies
            )
        }

        globalSymbolsForReprocessing.toList()
    }

}