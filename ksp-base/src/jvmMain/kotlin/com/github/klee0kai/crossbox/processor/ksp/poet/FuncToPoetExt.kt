package com.github.klee0kai.crossbox.processor.ksp.poet

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

val KSFunctionDeclaration.isSuspend: Boolean
    get() = modifiers.contains(Modifier.SUSPEND)

fun KSFunctionDeclaration.toMemberName() = MemberName(packageName.asString(), simpleName.asString())

fun FunSpec.Builder.declareSameParameters(
    function: KSFunctionDeclaration,
) = apply {
    function.returnType?.resolve()?.toTypeName()?.let { returns(it) }
    function.extensionReceiver?.resolve()?.toTypeName()?.let { receiver(it) }

    function.parameters.forEach { param ->
        addParameter(
            ParameterSpec.builder(
                name = param.name?.asString() ?: "",
                type = param.type.resolve().toTypeName(),
            ).apply {
                if (param.isVararg) addModifiers(KModifier.VARARG)
            }.build()
        )
    }
}
