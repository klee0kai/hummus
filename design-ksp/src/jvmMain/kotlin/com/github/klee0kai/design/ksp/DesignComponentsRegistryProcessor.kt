@file:OptIn(KspExperimental::class)

package com.github.klee0kai.design.ksp

import androidx.compose.runtime.Composable
import com.github.klee0kai.crossbox.processor.common.findCommonPgk
import com.github.klee0kai.crossbox.processor.exceptions.forEachKsNode
import com.github.klee0kai.crossbox.processor.ksp.arch.GenSpec
import com.github.klee0kai.crossbox.processor.ksp.arch.SymbolsToProcess
import com.github.klee0kai.crossbox.processor.ksp.arch.TargetSymbolProcessor
import com.github.klee0kai.crossbox.processor.ksp.poet.toMemberName
import com.github.klee0kai.crossbox.processor.poet.genFileSpec
import com.github.klee0kai.crossbox.processor.poet.genGetter
import com.github.klee0kai.crossbox.processor.poet.genObject
import com.github.klee0kai.crossbox.processor.poet.genProperty
import com.github.klee0kai.hummus.design.core.ComponentParameter
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class DesignComponentsRegistryProcessor : TargetSymbolProcessor {

    private fun buildParameterTypeCode(resolvedType: KSType, isNullable: Boolean): CodeBlock {
        val declaration = resolvedType.declaration
        val qualifiedName = declaration.qualifiedName?.asString() ?: "kotlin.Any"
        val parameterTypeName = ClassName("com.github.klee0kai.hummus.design.core", "ParameterType")

        return if (resolvedType.arguments.isNotEmpty()) {
            // Generic type with type arguments
            val typeArgs = resolvedType.arguments.mapNotNull { arg ->
                val argType = arg.type?.resolve()
                if (argType != null) {
                    buildParameterTypeCode(argType, argType.isMarkedNullable)
                } else {
                    null
                }
            }

            val code = CodeBlock.builder()
            code.add("%T.Generic(%T::class, listOf(\n", parameterTypeName, ClassName.bestGuess(qualifiedName))
            typeArgs.forEachIndexed { index, argCode ->
                code.add("          %L", argCode)
                if (index < typeArgs.size - 1) code.add(",")
                code.add("\n")
            }
            code.add("        ), %L)", isNullable)
            code.build()
        } else {
            // Simple type
            CodeBlock.of(
                "%T.Simple(%T::class, %L)",
                parameterTypeName,
                ClassName.bestGuess(qualifiedName),
                isNullable
            )
        }
    }


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
                // Note: Not adding @DebugOnly to allow access from UI components
                // addAnnotation(DebugOnly::class.asClassName())

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
                                val resolvedType = param.type.resolve()
                                val isNullable = resolvedType.isMarkedNullable
                                val declaration = resolvedType.declaration

                                val isParamComposable = param.annotations.any {
                                    it.annotationType.resolve().toTypeName() == Composable::class.asClassName()
                                }
                                val hasDefault = param.hasDefault
                                val paramName = param.name?.asString() ?: ""

                                // Генерируем ParameterType код
                                val typeCode = buildParameterTypeCode(resolvedType, isNullable)

                                addCode("      %T(\n", ComponentParameter::class.asClassName())
                                addCode("        name = %S,\n", paramName)
                                addCode("        type = %L,\n", typeCode)
                                addCode("        typeString = %S,\n", paramType)
                                addCode("        isComposable = %L,\n", isParamComposable)
                                addCode("        hasDefault = %L\n", hasDefault)
                                addCode("      ),\n")
                            }

                            addCode("    ),\n")
                            addCode("    invoker = { params ->\n")
                            addCode("      %M(\n", func.toMemberName())
                            func.parameters.forEach { param ->
                                val paramName = param.name?.asString() ?: ""
                                val resolvedType = param.type.resolve()
                                val isFunction = resolvedType.declaration.qualifiedName?.asString()
                                    ?.startsWith("kotlin.Function") == true

                                if (!isFunction) {
                                    val paramTypeName = resolvedType.toTypeName()
                                    addCode(
                                        "        %L = params[%S] as %T,\n",
                                        paramName,
                                        paramName,
                                        paramTypeName
                                    )
                                }
                            }
                            addCode("      )\n")
                            addCode("    }\n")
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
