package com.github.klee0kai.crossbox.processor.ksp.poet

import com.github.klee0kai.crossbox.processor.poet.codeBlock
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSValueArgument.invokeCodeBlock(): CodeBlock {
    return codeBlock {
        if (name != null) {
            add(name!!.asString())
            add("=")
        }
        add(value.anyValueCreateViaPoet())
    }
}

fun Any?.anyValueCreateViaPoet(): CodeBlock {
    val value = this
    return when {
        value is String -> CodeBlock.of("%S", value)
        value is Number -> CodeBlock.of("%L", value)
        value is ArrayList<*> -> {
            val listParams = value.map { it!!.anyValueCreateViaPoet() }.joinToCode(", ")
            CodeBlock.of("arrayOf(%L)", listParams)
        }

        value is List<*> -> {
            val listParams = value.map { it!!.anyValueCreateViaPoet() }.joinToCode(", ")
            CodeBlock.of("listOf(%L)", listParams)
        }

        value is KSType -> {
            val type = (value as KSType)
            CodeBlock.of("%T::class", type.toTypeName())
        }

        value != null -> CodeBlock.of("%L", value)
        else -> CodeBlock.of("null")
    }
}