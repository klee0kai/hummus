package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.CodeBlock

@PoetDsl
fun codeBlock(
    block: CodeBlock.Builder.() -> Unit,
) = CodeBlock.builder().apply(block).build()
