package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Extracts the raw (non-parameterized) type from this TypeName.
 *
 * For parameterized types like `List<String>`, returns the base type `List`.
 * For wildcard types, returns the raw type of the first upper bound.
 * For other types, returns a non-nullable copy of the type.
 *
 * @return The raw type name without type parameters and non-nullable
 */
fun TypeName.rawType(): TypeName {
    val typeName = this
    if (typeName is ParameterizedTypeName) {
        return typeName.rawType
    }
    if (typeName is WildcardTypeName) {
        if (!typeName.outTypes.isEmpty()) return typeName.outTypes.first().rawType()
    }
    return typeName.copy(nullable = false)
}

/**
 * Extracts the raw type of the first type argument from this TypeName.
 *
 * For parameterized types like `List<String>`, returns the raw type of the first argument `String`.
 * For wildcard types, returns the raw type of the first upper bound.
 * For other types or if no type arguments exist, returns a non-nullable copy of the type.
 *
 * @return The raw type of the first type argument, or the type itself if no arguments present
 */
fun TypeName.argRawType(): TypeName {
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