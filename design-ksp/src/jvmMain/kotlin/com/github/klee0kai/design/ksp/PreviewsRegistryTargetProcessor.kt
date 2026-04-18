@file:OptIn(KspExperimental::class)

package com.github.klee0kai.design.ksp

import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.crossbox.processor.common.findCommonPgk
import com.github.klee0kai.crossbox.processor.exceptions.forEachAnnotated
import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.poet.*
import com.github.klee0kai.hummus.design.core.FoundPreviewMethod
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import androidx.compose.desktop.ui.tooling.preview.Preview as OldPreview

class PreviewsRegistryTargetProcessor : TargetSymbolProcessor {

    override suspend fun findSymbolsToProcess(
        resolver: Resolver,
    ): SymbolsToProcess {
        val previewSymbols = resolver
            .getSymbolsWithAnnotation(Preview::class.qualifiedName!!)
            .groupBy { it.validate() }

        val oldPreviewSymbols = resolver
            .getSymbolsWithAnnotation(OldPreview::class.qualifiedName!!)
            .groupBy { it.validate() }

        val annotatedSymbols = previewSymbols + oldPreviewSymbols
        return SymbolsToProcess(
            symbolsForProcessing = annotatedSymbols[true].orEmpty(),
            symbolsForReprocessing = annotatedSymbols[false].orEmpty(),
            processOnlyTogether = true,
        )
    }

    override suspend fun multiSymbolsProcess(
        targetSymbols: List<KSAnnotated>,
        resolver: Resolver,
        options: Map<String, String>,
        logger: KSPLogger
    ): GenSpec {
        val commonPkg = targetSymbols
            .mapNotNull { it.containingFile?.packageName?.asString() }
            .findCommonPgk()

        val fileOwners = targetSymbols.mapNotNull { it.containingFile }

        val fileSpec = genFileSpec(commonPkg, "FoundPreviews") {

            genObject(ClassName(packageName, "FoundPreviews")) {

                genProperty(
                    "previews",
                    type = Sequence::class.asClassName()
                        .parameterizedBy(FoundPreviewMethod::class.asClassName()),
                ) {
                    genGetter {
                        genControlFlow("sequence<%T>", FoundPreviewMethod::class.asClassName()) {
                            targetSymbols.forEachAnnotated { idx, it ->
                                val func = it as? KSFunctionDeclaration ?: error("should be a function")

                                val memo = MemberName(
                                    func.packageName.asString(),
                                    func.simpleName.asString(),
                                )

                                addStatement(
                                    "yield( FoundPreviewMethod( " +
                                            "pkg = %S,\n " +
                                            "methodName = %S,\n " +
                                            "annotations = %L ,\n " +
                                            "param = %L , \n " +
                                            "paramIdx = %L , \n " +
                                            ") { \n " +
                                            "%M( %L ) \n " +
                                            "} ) ",
                                    memo.packageName,
                                    memo.simpleName,
                                    "listOf()",
                                    "null",
                                    "1",
                                    memo,
                                    "",
                                )

                            }
                        }
                    }
                }
            }

        }



        return GenSpec(
            fileSpec = fileSpec,
            // https://kotlinlang.org/docs/ksp-incremental.html
            dependencies = Dependencies(
                aggregating = true,
                sources = fileOwners.toTypedArray(),
            ),
        )
    }


}

