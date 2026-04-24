package com.github.klee0kai.crossbox.processor.ksp

import com.github.klee0kai.hummus.collections.removeDoubles
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

/**
 * Finds a constructor in the class that matches the given parameter types.
 *
 * Searches for a constructor (init function) whose parameter types match the provided list.
 * Parameters with default values are ignored in the matching.
 *
 * @param parameters list of parameter types to match
 * @return the matching constructor or null if not found
 */
fun KSClassDeclaration.findConstructor(
    parameters: List<KSType>,
): KSFunctionDeclaration? = getDeclaredFunctions().firstOrNull { function ->
    function.simpleName.asString() == "<init>"
            && function.parameters.all { it.type.resolve() in parameters || it.hasDefault }
}

/**
 * Checks if this declaration matches any of the provided class types.
 *
 * @param cl variable number of KClass types to check against
 * @return true if this declaration matches any of the provided types
 */
fun KSDeclaration.isAnyType(
    vararg cl: KClass<*>,
) = cl.any { isType(it) }

/**
 * Checks if this declaration represents the given Kotlin class type.
 *
 * Compares the fully qualified name of this declaration with the qualified name of the class.
 *
 * **Usage example:**
 * ```kotlin
 * if (classDeclaration.isType(String::class)) {
 *     println("This is a String class")
 * }
 * ```
 *
 * @param cl the Kotlin class to check against
 * @return true if this declaration represents the given class
 */
fun KSDeclaration.isType(cl: KClass<*>): Boolean = qualifiedName?.asString() == cl.qualifiedName.toString()

/**
 * Checks if this declaration represents the given KotlinPoet class name.
 *
 * Compares the fully qualified name of this declaration with the class name.
 *
 * @param cl the KotlinPoet class name to check against
 * @return true if this declaration represents the given class
 */
fun KSDeclaration.isType(cl: ClassName): Boolean = qualifiedName?.asString() == cl.toString()

/**
 * Gets all methods (functions) from this class and its supertypes.
 *
 * Recursively collects all methods from the class hierarchy, optionally including
 * methods from [Object]/[Any] class. Removes duplicate methods based on signature.
 *
 * **Parameters:**
 * - By default excludes methods from [Object] and [Any]
 * - Deduplicates methods with the same signature from different parts of the hierarchy
 * - Can exclude specific methods by name
 *
 * **Usage example:**
 *
 * Get all methods except lifecycle methods:
 *
 * ```kotlin
 * val classDecl: KSClassDeclaration = ...
 *
 * // Get all methods except constructors and overridden methods from Any
 * val allMethods = classDecl.getAllMethods(
 *     includeObjectMethods = false,
 *     exceptNames = arrayOf("<init>")
 * )
 *
 * // Filter to find overridable methods
 * val overridableMethods = allMethods
 *     .filter { !it.modifiers.contains(Modifier.FINAL) }
 *     .toList()
 * ```
 *
 * @param includeObjectMethods if true, includes methods from Object/Any classes
 * @param allowDoubles if true, keeps duplicate method signatures
 * @param exceptNames array of method names to exclude
 * @return sequence of all discovered methods
 */
fun KSClassDeclaration.getAllMethods(
    includeObjectMethods: Boolean = false,
    allowDoubles: Boolean = false,
    vararg exceptNames: String = emptyArray(),
): Sequence<KSFunctionDeclaration> = sequence<KSFunctionDeclaration> {
    val cl = this@getAllMethods
    if (!includeObjectMethods && cl.qualifiedName?.asString() in listOf(
            Object::class.qualifiedName,
            Any::class.qualifiedName
        )
    ) {
        return@sequence
    }

    val allMethods = mutableListOf<KSFunctionDeclaration>()
    getAllSuperTypes().forEach { superType ->
        allMethods.addAll(
            (superType.declaration as KSClassDeclaration)
                .getAllMethods(
                    includeObjectMethods = includeObjectMethods,
                    allowDoubles = allowDoubles,
                    exceptNames = exceptNames,
                )
        )
    }
    allMethods.addAll(getDeclaredFunctions())

    yieldAll(
        allMethods
            .filter {
                it.simpleName.asString() !in exceptNames
            }
            .removeDoubles { it1, it2 ->
                it1.isSameMethods(it2)
            }
    )
}


/**
 * Checks if this class is a child of (extends or implements) the given parent type.
 *
 * Recursively searches the class hierarchy to determine if this class is a subtype
 * of the provided parent class.
 *
 * **Usage example:**
 * ```kotlin
 * if (classDeclaration.isChildOf(ClassName("java.util", "AbstractList"))) {
 *     println("This class extends AbstractList")
 * }
 * ```
 *
 * @param parentType the parent class type to check against
 * @return true if this class extends or implements the parent type
 */
fun KSClassDeclaration.isChildOf(
    parentType: ClassName,
): Boolean {
    if (toClassName() == parentType) return true
    superTypes.forEach { type ->
        if (type.resolve().toClassName() == type) return true
        if ((type.resolve().declaration as? KSClassDeclaration)?.isChildOf(parentType) == true) return true
    }
    return false
}

/**
 * Checks if this type is the Kotlin [Unit] type.
 *
 * Unit represents the absence of a meaningful return value, equivalent to void in Java.
 */
val KSType.isUnit: Boolean get() = declaration.qualifiedName?.asString() == "kotlin.Unit"

/**
 * Checks if this type is NOT a primitive type.
 *
 * Returns true for all types except:
 * - Kotlin primitives: Boolean, Byte, Short, Int, Long, Char, Float, Double, Unit
 * - Java primitives: java.lang.Boolean, java.lang.Byte, java.lang.Short, etc.
 *
 * This is useful for determining whether a type needs special handling
 * or can be treated as a reference type.
 */
val KSType.isNotPrimitive: Boolean
    get() {
        return declaration.qualifiedName?.asString() !in setOf(
            "java.lang.Boolean",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Character",
            "java.lang.Float",
            "java.lang.Double",
            "kotlin.Boolean",
            "kotlin.Byte",
            "kotlin.Short",
            "kotlin.Int",
            "kotlin.Long",
            "kotlin.Char",
            "kotlin.Float",
            "kotlin.Double",
            "kotlin.Unit",
        )
    }
