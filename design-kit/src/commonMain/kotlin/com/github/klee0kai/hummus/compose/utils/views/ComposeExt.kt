package com.github.klee0kai.hummus.compose.utils.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.github.klee0kai.hummus.cleanable.CleanableDelegate
import com.github.klee0kai.hummus.ref.WeakDelegate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.ReadWriteProperty

@Composable
fun <T : R, R> Deferred<T>.collectAsState(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this, context) {
    if (context == EmptyCoroutineContext) {
        value = await()
    } else withContext(context) {
        value = await()
    }
}

@Composable
fun <T : R, R> Flow<T>.collectAsState(
    key: Any?,
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState((this as? StateFlow)?.value ?: initial, key, context) {
    if (context == EmptyCoroutineContext) {
        collect { value = it }
    } else withContext(context) {
        collect { value = it }
    }
}

@Composable
fun <T> accumulate(init: T, calculation: (old: T) -> T): State<T> {
    val state = remember { mutableStateOf(init) }
    val newState = calculation(state.value)
    if (newState != state.value) {
        state.value = calculation(state.value)
    }
    return state
}

@Composable
fun <T> rememberDerivedStateOf(
    key1: Any? = Unit,
    calculation: () -> T
) = remember(key1) {
    derivedStateOf(calculation)
}

@Composable
@NonRestartableComposable
fun <T> rememberOnScreenRef(
    key1: Any? = Unit,
    block: @DisallowComposableCalls () -> T,
): ReadWriteProperty<Any?, T?> {
    val cached = remember(key1) { CleanableDelegate<T?>(block()) }
    DisposableEffect(key1 = key1) {
        onDispose {
            cached.clean()
        }
    }
    if (cached.value == null) cached.value = block()

    return cached
}

@Composable
fun rememberTickerOf(trigger: () -> Boolean): State<Int> {
    var lastState by remember { mutableStateOf(trigger()) }
    val updateTicker = remember { mutableIntStateOf(0) }
    val newState = trigger()
    if (lastState != newState) {
        lastState = newState
        if (newState) {
            updateTicker.value++
        }
    }
    return updateTicker
}


@NonRestartableComposable
@Composable
inline fun Modifier.thenIfCrossFade(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier,
): Modifier {
    val target by animateTargetFaded(target = condition)
    return if (target.current) {
        block().alpha(target.alpha)
    } else {
        this.alpha(target.alpha)
    }
}

fun Modifier.tappable(onTap: ((Offset) -> Unit)? = null) =
    this.pointerInput(Unit) { detectTapGestures(onTap = onTap) }

@Composable
fun Modifier.animatedBackground(condition: Boolean, background: Color): Modifier {
    val alpha by animateAlphaAsState(target = condition)
    return this.background(background.copy(alpha = background.alpha * alpha))
}

val <T> CompositionLocal<T>.currentRef: WeakDelegate<T>
    @Composable get() = WeakDelegate(current)


inline fun <reified Res, reified T : Res> T.thenIf(
    condition: Boolean,
    block: T.() -> Res,
) = if (condition) block() else this
