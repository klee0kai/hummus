package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec

@PoetDsl
fun CodeBlock.Builder.genControlFlow(
    controlFlow: String,
    vararg args: Any,
    block: CodeBlock.Builder.() -> Unit,
) = apply {
    beginControlFlow(controlFlow, *args)
        .add(CodeBlock.builder().apply(block).build())
        .endControlFlow()
        .add("\n")
}

@PoetDsl
fun FunSpec.Builder.genControlFlow(
    controlFlow: String,
    vararg args: Any,
    block: CodeBlock.Builder.() -> Unit,
) = apply {
    beginControlFlow(controlFlow, *args)
        .addCode(CodeBlock.builder().apply(block).build())
        .endControlFlow()
}