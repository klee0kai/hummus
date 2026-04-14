package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec

/**
 * Marker annotation for DSL functions that build [PropertySpec].
 *
 * This annotation prevents accidental use of DSL functions outside of their intended scope
 * and provides IDE support for DSL-based code generation.
 *
 * @see <a href="https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker">Kotlin DSL documentation</a>
 */
@DslMarker
annotation class PropertySpecDsl

/**
 * Adds a custom getter to the property being built.
 *
 * **Usage example:**
 * ```kotlin
 * // Simple getter that returns computed value
 * genProperty("isActive", Boolean::class) {
 *     genGetter {
 *         addStatement("return status != %S", "INACTIVE")
 *     }
 * }
 *
 * // Extension property getter from KSP processor
 * genProperty("classInfo", ClassInfo::class.asClassName()) {
 *     receiver(MyClass::class.asClassName())
 *     genGetter {
 *         addCode(
 *             "return %T(packageName = %S, simpleName = %S)",
 *             ClassInfo::class.asClassName(),
 *             "com.example",
 *             "MyClass"
 *         )
 *     }
 * }
 * ```
 *
 * @param block optional DSL block for configuring the getter
 * @return the created getter [FunSpec]
 */
@PropertySpecDsl
fun PropertySpec.Builder.genGetter(
    block: FunSpec.Builder.() -> Unit = {}
): FunSpec {
    return FunSpec.getterBuilder()
        .apply(block)
        .build()
        .also {
            getter(it)
        }
}

/**
 * Adds a custom setter to the property being built.
 *
 * **Usage example:**
 * ```kotlin
 * genProperty("value", Int::class) {
 *     genSetter {
 *         addParameter("value", Int::class)
 *         addStatement("field = value")
 *     }
 * }
 * ```
 *
 * @param block optional DSL block for configuring the setter
 * @return the created setter [FunSpec]
 */
@PropertySpecDsl
fun PropertySpec.Builder.genSetter(
    block: FunSpec.Builder.() -> Unit = {}
): FunSpec {
    return FunSpec.setterBuilder()
        .apply(block)
        .build()
        .also {
            setter(it)
        }
}