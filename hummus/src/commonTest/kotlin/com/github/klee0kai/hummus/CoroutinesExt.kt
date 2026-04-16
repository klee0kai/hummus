package com.github.klee0kai.hummus

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

expect fun runTest(
    timeout: Duration = 60.minutes,
    block: suspend CoroutineScope.() -> Unit,
)