@file:OptIn(KspExperimental::class)

package com.github.klee0kai.ksp.base.sample.processor

import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.poet.genFileSpec
import com.github.klee0kai.crossbox.processor.poet.genGetter
import com.github.klee0kai.crossbox.processor.poet.genProperty
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.github.klee0kai.ksp.base.sample.processor.model.ClassInfo
import com.github.klee0kai.ksp.base.sample.processor.model.GenClassInfo

class ClassInfoTargetProcessor : TargetSymbolProcessor {

    override suspend fun findSymbolsToProcess(resolver: Resolver): SymbolsToProcess {
        val annotatedSymbols = resolver
            .getSymbolsWithAnnotation(GenClassInfo::class.asClassName().canonicalName)
            .groupBy { it.validate() }

        return SymbolsToProcess(
            symbolsForProcessing = annotatedSymbols[true].orEmpty(),
            symbolsForReprocessing = annotatedSymbols[false].orEmpty(),
        )
    }

    override suspend fun process(
        targetSymbol: KSAnnotated,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger
    ): GenSpec? {

        val fileOwner = targetSymbol.containingFile ?: return null
        val classDeclaration = targetSymbol as? KSClassDeclaration ?: return null

        val fileSpec = genFileSpec(
            packageName = classDeclaration.packageName.asString(),
            fileName = classDeclaration.simpleName.asString() + "Ext",
        ) {

            genProperty(
                name = "classInfo",
                type = ClassInfo::class.asClassName(),
            ) {
                receiver(classDeclaration.toClassName())
                genGetter {
                    addCode(
                        "return %T( packageName = %S, simpleName = %S )",
                        ClassInfo::class.asClassName(),
                        classDeclaration.packageName.asString(),
                        classDeclaration.simpleName.asString(),
                    )
                }
            }
        }


        return GenSpec(
            fileSpec = fileSpec,
            // https://kotlinlang.org/docs/ksp-incremental.html
            dependencies = Dependencies(aggregating = false, fileOwner),
        )
    }
}