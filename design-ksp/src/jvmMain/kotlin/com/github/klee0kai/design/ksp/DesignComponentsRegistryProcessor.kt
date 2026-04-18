@file:OptIn(KspExperimental::class)

package com.github.klee0kai.design.ksp

import androidx.compose.runtime.Composable
import com.github.klee0kai.crossbox.processor.common.findCommonPgk
import com.github.klee0kai.crossbox.processor.exceptions.forEachKsNode
import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.ksp.poet.toMemberName
import com.github.klee0kai.crossbox.processor.poet.*
import com.github.klee0kai.hummus.design.core.DebugOnly
import com.github.klee0kai.hummus.design.core.ComponentParameter
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName

class DesignComponentsRegistryProcessor : TargetSymbolProcessor {

    override suspend fun findSymbolsToProcess(
        resolver: Resolver,
    ): SymbolsToProcess {
        val designComponentSymbols = resolver
            .getSymbolsWithAnnotation("com.github.klee0kai.hummus.design.core.DesignComponent")
            .groupBy { it.validate() }

        val annotatedSymbols = designComponentSymbols
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

        val fileSpec = genFileSpec(commonPkg, "DesignComponentsRegistry") {
            genObject(ClassName(packageName, "DesignComponentsRegistry")) {
                addAnnotation(DebugOnly::class.asClassName())

                genProperty(
                    "components",
                    type = List::class.asClassName()
                        .parameterizedBy(DesignComponentMethod::class.asClassName()),
                ) {
                    genGetter {
                        addCode("return listOf(\n")
                        targetSymbols.forEachKsNode { _, symbol ->
                            val func = symbol as? KSFunctionDeclaration ?: return@forEachKsNode
                            val isComposable = func.annotations.any {
                                it.annotationType.resolve().toTypeName() == Composable::class.asClassName()
                            }

                            if (!isComposable) return@forEachKsNode

                            addCode("  %T(\n", DesignComponentMethod::class.asClassName())
                            addCode("    pkg = %S,\n", func.toMemberName().packageName)
                            addCode("    methodName = %S,\n", func.toMemberName().simpleName)
                            addCode("    parameters = listOf(\n")

                            func.parameters.forEach { param ->
                                val paramType = param.type.resolve().toTypeName().toString()
                                val isParamComposable = param.annotations.any {
                                    it.annotationType.resolve().toTypeName() == Composable::class.asClassName()
                                }
                                val hasDefault = param.hasDefault
                                val paramName = param.name?.asString() ?: ""

                                addCode("      %T(\n", ComponentParameter::class.asClassName())
                                addCode("        name = %S,\n", paramName)
                                addCode("        type = %S,\n", paramType)
                                addCode("        isComposable = %L,\n", isParamComposable)
                                addCode("        hasDefault = %L\n", hasDefault)
                                addCode("      ),\n")
                            }

                            addCode("    ),\n")
                            val allParamsHaveDefaults = func.parameters.all { it.hasDefault }
                            if (allParamsHaveDefaults) {
                                addCode("    content = { %M() }\n", func.toMemberName())
                            } else {
                                addCode("    content = { }\n")
                            }
                            addCode("  ),\n")
                        }
                        addCode(")\n")
                    }
                }
            }
        }

        return GenSpec(
            fileSpec = fileSpec,
            dependencies = Dependencies(
                aggregating = false,
                sources = fileOwners.toTypedArray(),
            ),
        )
    }
}
