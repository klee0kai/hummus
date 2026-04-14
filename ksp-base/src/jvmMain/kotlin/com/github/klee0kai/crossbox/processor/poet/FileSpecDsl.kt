package com.github.klee0kai.crossbox.processor.poet

import com.squareup.kotlinpoet.*

/**
 * Marker annotation for DSL functions that build [FileSpec].
 *
 * This annotation prevents accidental use of DSL functions outside of their intended scope
 * and provides IDE support for DSL-based code generation.
 *
 * @see <a href="https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker">Kotlin DSL documentation</a>
 */
@DslMarker
annotation class FileSpecDsl

/**
 * Creates a new [FileSpec] for generating a Kotlin file.
 *
 * This is the entry point for building a complete Kotlin file with the DSL.
 *
 * **Real-world example:**
 *
 * Generate an extension property that provides metadata about a class:
 *
 * ```kotlin
 * val classDecl: KSClassDeclaration = ... // From annotation processing
 *
 * val fileSpec = genFileSpec(
 *     packageName = classDecl.packageName.asString(),
 *     fileName = classDecl.simpleName.asString() + "Ext",
 * ) {
 *     genProperty(
 *         name = "classInfo",
 *         type = ClassInfo::class.asClassName(),
 *     ) {
 *         receiver(classDecl.toClassName())
 *         genGetter {
 *             addCode(
 *                 "return %T(packageName = %S, simpleName = %S)",
 *                 ClassInfo::class.asClassName(),
 *                 classDecl.packageName.asString(),
 *                 classDecl.simpleName.asString(),
 *             )
 *         }
 *     }
 * }
 *
 * // Generated code:
 * // val MyClass.classInfo: ClassInfo
 * //     get() = ClassInfo(packageName = "com.example", simpleName = "MyClass")
 * ```
 *
 * @param packageName the package name for the file
 * @param fileName the file name (without .kt extension)
 * @param block DSL block for configuring the [FileSpec.Builder]
 * @return the built [FileSpec]
 */
@FileSpecDsl
fun genFileSpec(
    packageName: String,
    fileName: String,
    block: FileSpec.Builder.() -> Unit,
): FileSpec {
    return FileSpec.builder(packageName, fileName)
        .also(block)
        .build()
}

/**
 * Adds a property to the file being built.
 *
 * @param name the property name
 * @param type the property type
 * @param modifiers optional modifiers (e.g., KModifier.PRIVATE, KModifier.CONST)
 * @param block optional DSL block for further configuration
 */
@FileSpecDsl
fun FileSpec.Builder.genProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
) {
    addProperty(
        PropertySpec.builder(name, type, *modifiers)
            .apply(block)
            .build()
    )
}

/**
 * Adds a class type to the file being built.
 *
 * @param className the class name
 * @param block optional DSL block for configuring the class
 */
@FileSpecDsl
fun FileSpec.Builder.genClass(
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
 * Adds an object (singleton) to the file being built.
 *
 * @param className the object name
 * @param block optional DSL block for configuring the object
 */
@FileSpecDsl
fun FileSpec.Builder.genObject(
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
 * Adds an interface to the file being built.
 *
 * @param className the interface name
 * @param block optional DSL block for configuring the interface
 */
@FileSpecDsl
fun FileSpec.Builder.genInterface(
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
 * Adds a top-level function to the file being built.
 *
 * @param name the function name
 * @param block optional DSL block for configuring the function
 */
@FileSpecDsl
fun FileSpec.Builder.genFun(
    name: String,
    block: FunSpec.Builder.() -> Unit = {},
) {
    addFunction(
        FunSpec.builder(name)
            .apply(block)
            .build()
    )
}


