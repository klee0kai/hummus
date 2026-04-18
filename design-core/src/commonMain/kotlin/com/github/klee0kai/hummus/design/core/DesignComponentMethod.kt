package com.github.klee0kai.hummus.design.core

import androidx.compose.runtime.Composable

data class ComponentParameter(
    val name: String,
    val type: String,
    val isComposable: Boolean = false,
    val hasDefault: Boolean = false,
) {
    fun isBoolean(): Boolean = type.contains("Boolean")
    fun isInt(): Boolean = type.contains("Int")
    fun isString(): Boolean = type.contains("String")
    fun isFloat(): Boolean = type.contains("Float")
    fun isDouble(): Boolean = type.contains("Double")
    fun isList(): Boolean = type.startsWith("kotlin.collections.List")
    fun isLambda(): Boolean = type.contains("(") && type.contains(")")
}

class DesignComponentMethod(
    val pkg: String,
    val methodName: String,
    val parameters: List<ComponentParameter>,
    val content: @Composable () -> Unit,
) {
    fun createBuilder(): ComponentParameterBuilder = ComponentParameterBuilder(this)

    fun parameterByName(name: String): ComponentParameter? = parameters.firstOrNull { it.name == name }

    fun requiredParameters(): List<ComponentParameter> = parameters.filter { !it.hasDefault }

    fun optionalParameters(): List<ComponentParameter> = parameters.filter { it.hasDefault }
}

class ComponentParameterBuilder(private val method: DesignComponentMethod) {
    private val values = mutableMapOf<String, Any?>()

    fun set(name: String, value: Any?): ComponentParameterBuilder {
        values[name] = value
        return this
    }

    fun setIfPresent(name: String, value: Any?): ComponentParameterBuilder {
        if (method.parameters.any { it.name == name }) {
            values[name] = value
        }
        return this
    }

    fun <T : Any> setOfType(name: String, value: T): ComponentParameterBuilder {
        val param = method.parameterByName(name)
        if (param != null) {
            values[name] = value
        }
        return this
    }

    fun getOrNull(name: String): Any? = values[name]

    fun get(name: String): Any? = values[name]
        ?: error("Parameter '$name' not set for method '${method.methodName}'")

    fun getMissingRequired(): List<String> {
        return method.requiredParameters()
            .filter { it.name !in values }
            .map { it.name }
    }

    fun isComplete(): Boolean = getMissingRequired().isEmpty()

    fun build(): Map<String, Any?> = values.toMap()

    fun buildOrThrow(): Map<String, Any?> {
        val missing = getMissingRequired()
        if (missing.isNotEmpty()) {
            error("Missing required parameters: ${missing.joinToString(", ")}")
        }
        return values.toMap()
    }
}
