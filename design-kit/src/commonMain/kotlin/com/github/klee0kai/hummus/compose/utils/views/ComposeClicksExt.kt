@file:OptIn(ExperimentalTime::class)

package com.github.klee0kai.hummus.compose.utils.views

import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
@NonRestartableComposable
fun rememberClick(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    launch: () -> Unit,
): () -> Unit = remember(key1, key2, key3, key4, key5, key6) {
    launch
}

@Composable
@NonRestartableComposable
fun rememberClickDebounced(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: () -> Unit,
): () -> Unit {
    var lastClickTime by remember { mutableStateOf<Instant?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        {
            val now = Clock.System.now()
            if (lastClickTime?.let { now - it < debounce } != true) {
                launch()
                lastClickTime = now
            }
        }
    }
}


@Composable
@NonRestartableComposable
fun <Arg> rememberClickDebouncedArg(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: (Arg) -> Unit,
): (Arg) -> Unit {
    var lastClickTime by remember { mutableStateOf<Instant?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        { arg: Arg ->
            val now = Clock.System.now()
            if (lastClickTime?.let { now - it < debounce } != true) {
                launch(arg)
                lastClickTime = now
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun <Arg> rememberClickArg(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    launch: (Arg) -> Unit,
): (Arg) -> Unit = rememberClickDebouncedArg(key1, key2, key3, key4, key5, key6, ZERO, launch)

@Composable
@NonRestartableComposable
fun <Arg, Arg2> rememberClickDebouncedArg2(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: (Arg, Arg2) -> Unit,
): (Arg, Arg2) -> Unit {
    var lastClickTime by remember { mutableStateOf<Instant?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        { arg: Arg, arg2: Arg2 ->
            val now = Clock.System.now()
            if (lastClickTime?.let { now - it < debounce } != true) {
                launch(arg, arg2)
                lastClickTime = now
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun <Cons> rememberClickDebouncedCons(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: Cons.() -> Unit,
): Cons.() -> Unit {
    var lastClickTime by remember { mutableStateOf<Instant?>(null) }
    return remember(key1, key2, key3, key4, key5, key6) {
        {
            val now = Clock.System.now()
            if (lastClickTime?.let { now - it < debounce } != true) {
                launch()
                lastClickTime = now
            }
        }
    }
}


@Composable
@NonRestartableComposable
fun Modifier.rememberClickableDebounced(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    debounce: Duration = 500.milliseconds,
    launch: () -> Unit,
): Modifier = this.clickable(
    onClick = rememberClickDebounced(key1, key2, key3, key4, key5, key6, debounce, launch)
)
