package com.github.klee0kai.hummus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Duration

actual fun runTest(
    timeout: Duration,
    block: suspend CoroutineScope.() -> Unit,
    ) {
    GlobalScope.launch {
        block()
    }
}