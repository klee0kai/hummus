package com.github.klee0kai.crossbox.processor.poet

import com.github.klee0kai.crossbox.processor.exceptions.wrapKsNoteInfo
import com.github.klee0kai.crossbox.processor.ksp.isSuspend
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Marker annotation for DSL functions that build [TypeSpec].
 *
 * This annotation prevents accidental use of DSL functions outside of their intended scope
 * and provides IDE support for DSL-based code generation.
 *
 * @see <a href="https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker">Kotlin DSL documentation</a>
 */
@DslMarker
annotation class TypeSpecDsl

/**
 * Adds a property to the type being built.
 *
 * @param name the property name
 * @param type the property type
 * @param modifiers optional modifiers (e.g., KModifier.PRIVATE, KModifier.LATEINIT)
 * @param block optional DSL block for further configuration
 * @return the created [PropertySpec]
 */
@PoetDsl
fun TypeSpec.Builder.genProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
): PropertySpec {
    return PropertySpec.builder(name, type, *modifiers)
        .apply(block)
        .build()
        .also {
            addProperty(it)
        }
}

/**
 * Adds a nested class to the type being built.
 *
 * @param className the class name
 * @param block optional DSL block for configuring the class
 */
@PoetDsl
fun TypeSpec.Builder.genClass(
    className: ClassName,
    block: TypeSpec.Builder.() -> Unit = {},
) {
    addType(
        TypeSpec.classBuilder(className)
            .apply(block)
            .build()
    )
}

/**
 * Adds a nested object (singleton) to the type being built.
 *
 * @param className the object name
 * @param block optional DSL block for configuring the object
 */
@PoetDsl
fun TypeSpec.Builder.genObject(
    className: ClassName,
    block: TypeSpec.Builder.() -> Unit = {},
) {
    addType(
        TypeSpec.objectBuilder(className)
            .apply(block)
            .build()
    )
}

/**
 * Adds a nested interface to the type being built.
 *
 * @param className the interface name
 * @param block optional DSL block for configuring the interface
 */
@PoetDsl
fun TypeSpec.Builder.genInterface(
    className: ClassName,
    block: TypeSpec.Builder.() -> Unit = {},
) {
    addType(
        TypeSpec.interfaceBuilder(className)
            .apply(block)
            .build()
    )
}

/**
 * Adds a member function to the type being built.
 *
 * @param name the function name
 * @param block optional DSL block for configuring the function
 */
@PoetDsl
fun TypeSpec.Builder.genFun(
    name: String,
    block: FunSpec.Builder.() -> Unit = {},
) {
    addFunction(
        FunSpec.builder(name)
            .apply(block)
            .build()
    )
}

/**
 * Adds a primary constructor to the type being built.
 *
 * A type can have at most one primary constructor. The primary constructor
 * properties can be declared inline using [FunSpec.Builder.addParameter].
 *
 * **Usage example:**
 * ```kotlin
 * genClass(ClassName("com.example", "MyClass")) {
 *     genPrimaryConstructor {
 *         addParameter("name", String::class)
 *         addParameter("age", Int::class)
 *     }
 * }
 * ```
 *
 * @param block optional DSL block for configuring the constructor
 */
@PoetDsl
fun TypeSpec.Builder.genPrimaryConstructor(
    block: FunSpec.Builder.() -> Unit = {},
) {
    primaryConstructor(
        FunSpec.constructorBuilder()
            .apply(block)
            .build()
    )
}

/**
 * Adds a secondary constructor to the type being built.
 *
 * Types can have multiple secondary constructors. Secondary constructors
 * typically delegate to the primary constructor or other constructors.
 *
 * @param block optional DSL block for configuring the constructor
 */
@PoetDsl
fun TypeSpec.Builder.genConstructor(
    block: FunSpec.Builder.() -> Unit = {},
) {
    addFunction(
        FunSpec.constructorBuilder()
            .apply(block)
            .build()
    )
}

@PoetDsl
fun TypeSpec.Builder.genOverrideFun(
    func: KSFunctionDeclaration,
    block: FunSpec.Builder.() -> Unit = {},
) {
    genFun(func.simpleName.asString()) {
        addModifiers(KModifier.OVERRIDE)
        if (func.isSuspend) addModifiers(KModifier.SUSPEND)
        declareSameParameters(func)
        func.returnType?.resolve()?.toTypeName()?.let { returns(it) }
        wrapKsNoteInfo(func) { block() }
    }
}
