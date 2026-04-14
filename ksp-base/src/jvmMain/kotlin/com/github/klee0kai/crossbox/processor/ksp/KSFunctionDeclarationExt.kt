package com.github.klee0kai.crossbox.processor.ksp

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

/**
 * Checks if this function has the same signature as another function.
 *
 * Compares function names, parameter count, and parameter types.
 * Return type and modifiers are not compared.
 *
 * **Usage example:**
 * ```kotlin
 * if (method1.isSameMethods(method2)) {
 *     println("These methods override each other")
 * }
 * ```
 *
 * @param other the other function to compare with
 * @return true if both functions have the same name and parameter types
 */
fun KSFunctionDeclaration.isSameMethods(
    other: KSFunctionDeclaration,
): Boolean {
    if (simpleName != other.simpleName
        || parameters.size != other.parameters.size
    ) {
        return false
    }
    for (idx in parameters.indices) {
        if (parameters[idx].type != other.parameters[idx].type) {
            return false
        }
    }

    return true
}

/**
 * Creates a string of invoke arguments matching available variables.
 *
 * For each parameter in this function, finds a matching variable in [availableVariables]
 * with the same type and creates a named argument assignment (e.g., "paramName = varName").
 *
 * Parameters without matching variables are skipped.
 *
 * **Usage example:**
 * ```kotlin
 * val arguments = myFunction.joinInvokeArguments(availableVariables)
 * // Result: "name = userName, age = userAge"
 * ```
 *
 * @param availableVariables list of available variables/parameters to match with
 * @return comma-separated string of named arguments
 */
fun KSFunctionDeclaration.joinInvokeArguments(
    availableVariables: List<KSValueParameter>,
): String {
    return parameters.mapNotNull { parameter ->
        val availableVariable = availableVariables.firstOrNull { it.type.resolve() == parameter.type.resolve() }
        if (availableVariable != null) {
            "${parameter.name!!.asString()} = ${availableVariable.name!!.asString()}"
        } else {
            null
        }
    }.joinToString(", ")
}

