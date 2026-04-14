package com.github.klee0kai.crossbox.processor

import com.github.klee0kai.hummus.coroutine.LaunchConductor
import com.github.klee0kai.crossbox.processor.exceptions.KspBaseException
import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.ksp.arch.filter
import com.github.klee0kai.crossbox.processor.ksp.arch.forceProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.nowTakeOnly
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

class TargetKSPProcessor(
    private val targetProcessors: Array<TargetSymbolProcessor>,
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    val oneRunSymbolsCount: Int = options["oneRunSymbolsCount"]?.toInt()
        ?: max(Runtime.getRuntime().availableProcessors(), 4),

    val multithread: Boolean = options["multithread"]?.toBoolean() ?: false,
    val debug: Boolean = options["debug"]?.toBoolean() ?: false,
    var debugPkgFilter: String? = options["debugPkgFilter"],
) : SymbolProcessor {

    val dispatcher by lazy { if (multithread) Dispatchers.Default else Dispatchers.Unconfined }

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

                    try {
                        processor.multiSymbolsProcess(
                            targetSymbol = symbols.symbolsForProcessing,
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