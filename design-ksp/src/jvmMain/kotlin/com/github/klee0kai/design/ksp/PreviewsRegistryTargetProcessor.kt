@file:OptIn(KspExperimental::class)

package com.github.klee0kai.design.ksp

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.github.klee0kai.crossbox.processor.common.findCommonPgk
import com.github.klee0kai.crossbox.processor.exceptions.forEachKsNode
import com.github.klee0kai.crossbox.processor.exceptions.mapKsNode
import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.ksp.poet.invokeCodeBlock
import com.github.klee0kai.crossbox.processor.ksp.poet.toMemberName
import com.github.klee0kai.crossbox.processor.poet.*
import com.github.klee0kai.hummus.design.core.DebugOnly
import com.github.klee0kai.hummus.design.core.FoundPreviewMethod
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
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
                addAnnotation(DebugOnly::class.asClassName())

                genProperty(
                    "previews",
                    type = Sequence::class.asClassName()
                        .parameterizedBy(FoundPreviewMethod::class.asClassName()),
                ) {
                    genGetter {
                        addCode("return ")
                        genControlFlow("sequence<%T>", FoundPreviewMethod::class.asClassName()) {
                            targetSymbols.forEachKsNode { idx, it ->
                                val func = it as? KSFunctionDeclaration ?: error("should be a function")

                                val annotationsCode = func.annotations.mapKsNode { index, annotation ->
                                    val annotationTypeName = annotation
                                        .annotationType
                                        .resolve()
                                        .toTypeName()
                                    val args = annotation.arguments
                                        .mapKsNode { idx, arg -> arg.invokeCodeBlock() }
                                        .joinToCode(separator = ",")
                                    CodeBlock.of("%T(%L)", annotationTypeName, args)
                                }.toList()
                                    .joinToCode(separator = ",\n")

                                val previewParameters = func.parameters.firstOrNull()
                                    ?.annotations
                                    ?.firstOrNull { it.annotationType.toTypeName() == PreviewParameter::class.asTypeName() }

                                val paramTypeProvider = (previewParameters?.arguments?.firstOrNull()?.value as? KSType)
                                    ?.toTypeName()

                                if (paramTypeProvider != null) {
                                    genControlFlow("%T().values.forEachIndexed { index, params ->", paramTypeProvider) {
                                        addFoundPreviewMethodStatement(
                                            func = func,
                                            annotationsCode = annotationsCode,
                                            paramIndex = "index",
                                            funParams = "params",
                                        )
                                    }
                                } else {
                                    addFoundPreviewMethodStatement(
                                        func = func,
                                        annotationsCode = annotationsCode,
                                    )
                                }

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
                aggregating = false,
                sources = fileOwners.toTypedArray(),
            ),
        )
    }

    private fun CodeBlock.Builder.addFoundPreviewMethodStatement(
        func: KSFunctionDeclaration,
        annotationsCode: CodeBlock,
        paramIndex: String = "0",
        funParams: String = "",
    ) {
        addStatement(
            "yield( FoundPreviewMethod( " +
                    "pkg = %S,\n " +
                    "methodName = %S,\n " +
                    "annotations = listOf(\n    $annotationsCode\n) ,\n " +
                    "param = %L , \n " +
                    "paramIdx = %L , \n " +
                    ") { \n " +
                    "%M( %L ) \n " +
                    "} ) ",
            func.toMemberName().packageName,
            func.toMemberName().simpleName,
            "null",
            paramIndex,
            func.toMemberName(),
            funParams,
        )
    }


}

