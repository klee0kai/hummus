package com.github.klee0kai.hummus

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
expect annotation class IgnoreJs()

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
expect annotation class IgnoreNative()


expect fun runTest(
    timeout: Duration = 60.minutes,
    block: suspend CoroutineScope.() -> Unit,
)