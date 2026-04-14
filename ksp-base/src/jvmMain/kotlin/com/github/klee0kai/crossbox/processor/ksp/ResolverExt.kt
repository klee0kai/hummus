package com.github.klee0kai.crossbox.processor.ksp

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

val KSFunctionDeclaration.isSuspend: Boolean get() = modifiers.contains(Modifier.SUSPEND)