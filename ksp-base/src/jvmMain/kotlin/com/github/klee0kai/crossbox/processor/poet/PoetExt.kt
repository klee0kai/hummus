package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec

/**
 * Converts a [PropertySpec] to a [ParameterSpec] with the same name and type.
 *
 * Useful when you need to pass a property as a constructor parameter.
 *
 * **Usage example:**
 * ```kotlin
 * val nameProperty = PropertySpec.builder("name", String::class).build()
 * val nameParameter = nameProperty.asParameter() // ParameterSpec(name="name", type=String)
 * ```
 *
 * @return a new [ParameterSpec] with the same name and type
 */
fun PropertySpec.asParameter(): ParameterSpec = ParameterSpec.builder(name, type).build()

/**
 * Initializes a property from a constructor parameter of the same name.
 *
 * Sets the property initializer to the parameter name, commonly used in primary constructors.
 *
 * **Usage example:**
 * ```kotlin
 * genPrimaryConstructor {
 *     addParameter("name", String::class)
 * }
 *
 * genProperty("name", String::class) {
 *     initFromConstructor() // Adds initializer: "name"
 * }
 * // Result: var name: String = name
 * ```
 *
 * @return this builder for chaining
 */
fun PropertySpec.Builder.initFromConstructor(): PropertySpec.Builder = apply { initializer(build().name) }

/**
 * Combines multiple [CodeBlock]s into a single code block.
 *
 * Concatenates all code blocks in order, useful for building complex code
 * fragments dynamically.
 *
 * **Usage example:**
 * ```kotlin
 * val blocks = listOf(
 *     CodeBlock.of("val x = 1"),
 *     CodeBlock.of("val y = 2")
 * )
 * val combined = blocks.toCodeBlock()
 * ```
 *
 * @return a single [CodeBlock] containing all code
 */
fun Collection<CodeBlock>.toCodeBlock(): CodeBlock {
    val blocks = this
    return CodeBlock.builder().apply {
        blocks.forEach {
            add(it)
        }
    }.build()
}
