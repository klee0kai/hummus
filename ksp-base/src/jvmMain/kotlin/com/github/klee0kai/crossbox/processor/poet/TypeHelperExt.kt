package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Extracts the raw type from a parameterized or wildcard type.
 *
 * For parameterized types like `List<String>`, returns the first type argument.
 * For wildcard types like `out String`, returns the first out type.
 * For simple types, returns the type itself (non-nullable).
 *
 * **Usage examples:**
 * ```kotlin
 * val listStringType = List::class.asClassName().parameterizedBy(String::class.asClassName())
 * listStringType.rawType() // Returns: String
 *
 * val wildcardType = WildcardTypeName.producerOf(Number::class.asClassName())
 * wildcardType.rawType() // Returns: Number
 *
 * val simpleType = String::class.asClassName()
 * simpleType.rawType() // Returns: String (non-nullable)
 * ```
 *
 * @return the raw type with nullability removed
 */
fun TypeName.rawType(): TypeName {
    val typeName = this
    if (typeName is ParameterizedTypeName) {
        if (!typeName.typeArguments.isEmpty()) {
            return typeName.typeArguments.first().rawType()
        }
    }
    if (typeName is WildcardTypeName) {
        if (!typeName.outTypes.isEmpty()) return typeName.outTypes.first().rawType()
    }
    return typeName.copy(nullable = false)
}