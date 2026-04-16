package com.github.klee0kai.hummus

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlinx.coroutines.test.runTest as rTest

actual fun runTest(
    timeout: Duration,
    block: suspend CoroutineScope.() -> Unit,
    ) {
    rTest(
        timeout = timeout
    ) { block() }
}
