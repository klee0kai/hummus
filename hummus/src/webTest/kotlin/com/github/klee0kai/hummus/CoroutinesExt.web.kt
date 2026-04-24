package com.github.klee0kai.hummus

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CLASS])
actual annotation class IgnoreNative actual constructor()

actual typealias IgnoreJs = kotlin.test.Ignore