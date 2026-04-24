package com.github.klee0kai.hummus

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlinx.coroutines.test.runTest as rTest

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CLASS])
actual annotation class IgnoreJs actual constructor()

actual typealias IgnoreNative = kotlin.test.Ignore

actual fun runTest(
    timeout: Duration,
    block: suspend CoroutineScope.() -> Unit,
) {
    rTest(
        timeout = timeout
    ) {
        backgroundScope.block()
    }
}

