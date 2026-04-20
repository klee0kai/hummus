package com.github.klee0kai.hummus.design.core

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

sealed interface ParameterType {
    val isNullable: Boolean
    val simpleNameString: String

    data class Simple(
        val kClass: KClass<*>,
        override val isNullable: Boolean = false,
    ) : ParameterType {
        override val simpleNameString: String
            get() = (if (isNullable) kClass.simpleName + "?" else kClass.simpleName) ?: "Unknown"

        fun matches(className: String): Boolean {
            val baseName = kClass.simpleName ?: return false
            return className.endsWith(".$baseName") || className == baseName
        }
    }

    data class Generic(
        val baseClass: KClass<*>,
        val typeArguments: List<ParameterType>,
        override val isNullable: Boolean = false,
    ) : ParameterType {
        override val simpleNameString: String
            get() {
                val args = typeArguments.joinToString(", ") { it.simpleNameString }
                val baseName = baseClass.simpleName ?: "Unknown"
                return if (isNullable) "$baseName<$args>?" else "$baseName<$args>"
            }

        fun isList(): Boolean = baseClass == List::class

        fun isMap(): Boolean = baseClass == Map::class

        fun isSet(): Boolean = baseClass == Set::class

        fun isFunction(): Boolean = baseClass.simpleName?.startsWith("Function") == true

        fun firstTypeArgument(): ParameterType? = typeArguments.firstOrNull()
        fun lastTypeArgument(): ParameterType? = typeArguments.lastOrNull()
        fun getTypeArgument(index: Int): ParameterType? =
            if (index >= 0 && index < typeArguments.size) typeArguments[index] else null
    }

    data class Nested(
        val outer: ParameterType,
        val inner: ParameterType,
        override val isNullable: Boolean = false,
    ) : ParameterType {
        override val simpleNameString: String
            get() {
                val nested = if (isNullable) "${outer.simpleNameString}.${inner.simpleNameString}?"
                else "${outer.simpleNameString}.${inner.simpleNameString}"
                return nested
            }
    }

    companion object {
        fun simple(kClass: KClass<*>, nullable: Boolean = false): ParameterType =
            Simple(kClass, nullable)

        fun generic(
            baseClass: KClass<*>,
            typeArguments: List<ParameterType>,
            nullable: Boolean = false
        ): ParameterType = Generic(baseClass, typeArguments, nullable)

        fun nested(outer: ParameterType, inner: ParameterType, nullable: Boolean = false): ParameterType =
            Nested(outer, inner, nullable)
    }
}

data class ComponentParameter(
    val name: String,
    val type: ParameterType,
    val typeString: String,  // String representation для совместимости
    val isComposable: Boolean = false,
    val hasDefault: Boolean = false,
) {
    fun isBoolean(): Boolean = when (type) {
        is ParameterType.Simple -> type.kClass == Boolean::class
        else -> false
    }

    fun isInt(): Boolean = when (type) {
        is ParameterType.Simple -> type.kClass == Int::class
        else -> false
    }

    fun isString(): Boolean = when (type) {
        is ParameterType.Simple -> type.kClass == String::class
        else -> false
    }

    fun isFloat(): Boolean = when (type) {
        is ParameterType.Simple -> type.kClass == Float::class
        else -> false
    }

    fun isDouble(): Boolean = when (type) {
        is ParameterType.Simple -> type.kClass == Double::class
        else -> false
    }

    fun isList(): Boolean = when (type) {
        is ParameterType.Generic -> type.baseClass == List::class
        else -> false
    }

    fun isMap(): Boolean = when (type) {
        is ParameterType.Generic -> type.baseClass == Map::class
        else -> false
    }

    fun isSet(): Boolean = when (type) {
        is ParameterType.Generic -> type.baseClass == Set::class
        else -> false
    }

    fun isLambda(): Boolean = when (type) {
        is ParameterType.Generic -> type.isFunction()
        else -> typeString.contains("(") && typeString.contains(")")
    }

    fun isNullable(): Boolean = type.isNullable

    fun getSimpleName(): String = type.simpleNameString
}

class DesignComponentMethod(
    val pkg: String,
    val methodName: String,
    val parameters: List<ComponentParameter>,
    val invoker: @Composable ((Map<String, Any?>) -> Unit)? = null,
) {
    fun createBuilder(): ComponentParameterBuilder = ComponentParameterBuilder(this)

    fun parameterByName(name: String): ComponentParameter? = parameters.firstOrNull { it.name == name }

    fun requiredParameters(): List<ComponentParameter> = parameters.filter { !it.hasDefault }

    fun optionalParameters(): List<ComponentParameter> = parameters.filter { it.hasDefault }

    @Composable
    fun invoke(paramsBuilder: ComponentParameterBuilder.() -> Unit) {
        val builder = createBuilder()
        parameters.forEach { param ->
            if (param.hasDefault && !param.isLambda()) {
                builder.set(param.name, getDefaultValue(param))
            }
        }
        builder.paramsBuilder()
        invoker?.invoke(builder.build())
    }

    companion object {
        private fun getDefaultValue(param: ComponentParameter): Any? = when {
            param.isBoolean() -> false
            param.isString() -> ""
            param.isInt() -> 0
            param.isFloat() -> 0f
            param.isDouble() -> 0.0
            param.isList() -> emptyList<String>()
            else -> null
        }
    }
}

class ComponentParameterBuilder(private val method: DesignComponentMethod) {
    private val values = mutableMapOf<String, Any?>()

    init {
        method.parameters.forEach { param ->
            if (param.type is ParameterType.Generic && param.type.isFunction()) {
                values[param.name] = {}
            }
        }
    }

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
