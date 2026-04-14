@file:OptIn(KspExperimental::class)

package com.github.klee0kai.crossbox.processor.target

import com.github.klee0kai.crossbox.core.CrossboxSerializable
import com.github.klee0kai.crossbox.processor.ksp.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.TargetFileProcessor
import com.github.klee0kai.crossbox.processor.poet.genFileSpec
import com.github.klee0kai.crossbox.processor.poet.genGetter
import com.github.klee0kai.crossbox.processor.poet.genLibComment
import com.github.klee0kai.crossbox.processor.poet.genProperty
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

class CrossboxSerializableProcessor : TargetFileProcessor {

    private val serializableClasses = mutableMapOf<String, KSClassDeclaration>()
    private val allDependencies = mutableListOf<KSFile>()
    private var symbolsToProcessCount = 0
    private var processedCount = 0

    override suspend fun findSymbolsToProcess(
        resolver: Resolver,
    ): SymbolsToProcess {

        val annotatedSymbols = resolver
            .getSymbolsWithAnnotation(CrossboxSerializable::class.asClassName().canonicalName)
            .groupBy { it.validate() }

        val validSymbols = annotatedSymbols[true].orEmpty()

        // Collect valid symbols for registry generation
        validSymbols.filterIsInstance<KSClassDeclaration>().forEach { classDecl ->
            serializableClasses[classDecl.qualifiedName?.asString() ?: return@forEach] = classDecl
        }

        symbolsToProcessCount = validSymbols.size
        processedCount = 0  // Reset for new round

        return SymbolsToProcess(
            symbolsForProcessing = validSymbols,
            symbolsForReprocessing = annotatedSymbols[false].orEmpty(),
        )
    }

    override suspend fun process(
        validSymbol: KSAnnotated,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger,
    ): GenSpec? {
        val classDeclaration = validSymbol as? KSClassDeclaration ?: return null
        val containingFile = validSymbol.containingFile ?: return null

        // Track dependencies
        if (containingFile !in allDependencies) {
            allDependencies.add(containingFile)
        }
        processedCount++

        // Generate registry file only on the last symbol processed
        if (processedCount < symbolsToProcessCount) {
            return null
        }

        if (serializableClasses.isEmpty()) {
            return null
        }


        // Generate registry file with all collected serializable classes
        val fileSpec = genFileSpec(
            packageName =
            fileName = "SerializerRegistry"
        ) {
            genLibComment()

            // Generate property with map of serializers
            genProperty(
                name = "serializerRegistry",
                type = Map::class.asClassName()
                    .parameterizedBy(
                        ClassName("kotlin.reflect", "KClass").parameterizedBy(STAR),
                        ClassName("kotlinx.serialization", "KSerializer").parameterizedBy(STAR)
                    ),
                KModifier.PUBLIC
            ) {
                genGetter {
                    addCode("return mapOf(\n")
                    serializableClasses.values.forEach { classDecl ->
                        val className = classDecl.toClassName()
                        addCode(
                            "%T::class to %T.serializer(),\n",
                            className,
                            className
                        )
                    }
                    addCode(")")
                }
            }
        }

        return GenSpec(
            fileSpec = fileSpec,
            dependencies = Dependencies(
                aggregating = true,
                *allDependencies.toTypedArray()
            ),
        )
    }
}

