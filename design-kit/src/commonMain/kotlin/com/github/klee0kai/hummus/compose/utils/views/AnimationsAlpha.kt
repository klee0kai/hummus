package com.github.klee0kai.hummus.compose.utils.views

import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.runtime.*
import com.github.klee0kai.hummus.compose.LocalHummusComposeConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

val AnimWarmUpTimeDefault
    @Composable
    get() = LocalHummusComposeConfig.current.warmUpTimeDefault

@Stable
@Immutable
data class TargetAlpha<T>(
    val current: T,
    val next: T,
    val alpha: Float = 0f,
)

fun <T> TargetAlpha<T>.hideOnTargetAlpha(vararg targetsToHide: T): Float {
    return when {
        targetsToHide.any { current == it } -> 0f
        targetsToHide.any { next == it } -> alpha
        else -> 1f
    }
}

fun <T> TargetAlpha<T>.visibleOnTargetAlpha(vararg targetsToVisible: T): Float {
    return when {
        targetsToVisible.any { current == it } -> alpha
        else -> 0f
    }
}


fun <T> TargetAlpha<T>.visibleOnTargetAlpha(targetsToVisible: T.() -> Boolean): Float {
    return when {
        targetsToVisible(current) != targetsToVisible(next) -> alpha
        targetsToVisible(current) -> 1f
        else -> 0f
    }
}

@Composable
inline fun animateAlphaAsState(
    target: Boolean,
    animationSpec: AnimationSpec<Float> = spring<Float>(),
    label: String = "",
) = animateFloatAsState(
    targetValue = if (target) 1f else 0f,
    animationSpec = animationSpec,
    label = label
)

@Composable
inline fun animateAlphaAsState(
    target: Boolean,
    warmUpTime: Duration = AnimWarmUpTimeDefault,
    animationSpec: AnimationSpec<Float> = spring<Float>(),
    label: String = "",
): State<Float> {
    var isWarmUp by remember { mutableStateOf(warmUpTime == Duration.ZERO) }
    LaunchedEffect(Unit) {
        if (!isWarmUp) {
            delay(warmUpTime)
            isWarmUp = true
        }
    }

    val animatedValue = if (!isWarmUp) {
        rememberDerivedStateOf { if (target) 1f else 0f }
    } else {
        animateFloatAsState(
            targetValue = if (target) 1f else 0f,
            animationSpec = animationSpec,
            label = label
        )
    }

    return animatedValue
}


@Composable
inline fun <T> Flow<T>.collectAsStateFaded(
    key: Any?,
    initial: T,
    warmUpTime: Duration = AnimWarmUpTimeDefault,
    context: CoroutineContext = EmptyCoroutineContext,
): State<TargetAlpha<T>> {
    val target by collectAsState(key = key, initial = initial, context = context)
    return animateTargetFaded(target, warmUpTime = warmUpTime)
}

@Composable
inline fun <T> rememberTargetFaded(
    warmUpTime: Duration = AnimWarmUpTimeDefault,
    noinline calculation: () -> T
): State<TargetAlpha<T>> {
    val target = rememberDerivedStateOf(calculation = calculation)
    return animateTargetFaded(target = target.value, warmUpTime = warmUpTime)
}


@Composable
inline fun rememberAlphaAnimate(
    animationSpec: AnimationSpec<Float> = spring<Float>(),
    warmUpTime: Duration = AnimWarmUpTimeDefault,
    noinline calculation: () -> Boolean,
): State<Float> {
    val target = rememberDerivedStateOf(calculation = calculation)
    return animateAlphaAsState(
        target = target.value,
        animationSpec = animationSpec,
        warmUpTime = warmUpTime,
    )
}

@Composable
@NonRestartableComposable
inline fun <T> State<TargetAlpha<T>>.smooth(): State<TargetAlpha<T>> {
    val alphaSmooth by animateFloatAsState(value.alpha)
    val targetAlphaSmooth = rememberDerivedStateOf { value.copy(alpha = alphaSmooth) }
    return targetAlphaSmooth
}

@Composable
inline fun <T> animateTargetFaded(
    target: T,
    warmUpTime: Duration = AnimWarmUpTimeDefault,
): State<TargetAlpha<T>> {
    var isWarmUp by remember { mutableStateOf(warmUpTime == Duration.ZERO) }
    LaunchedEffect(Unit) {
        if (!isWarmUp) {
            delay(warmUpTime)
            isWarmUp = true
        }
    }
    val animatedValue = animateTargetFaded(
        target = target,
        durationMillis = if (!isWarmUp) 0 else DefaultDurationMillis,
    )

    return animatedValue
}

@Composable
inline fun <T> animateTargetFaded(
    target: T,
    skipStates: List<T> = emptyList(),
    durationMillis: Int = DefaultDurationMillis,
): State<TargetAlpha<T>> {
    val targetAlphaState = remember { mutableStateOf(TargetAlpha(target, target, 1f)) }
    var targetAlpha by targetAlphaState
    var velocity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = target) {
        if (targetAlpha.current in skipStates) {
            targetAlpha = TargetAlpha(target, target, 1f)
            velocity = 0f
            return@LaunchedEffect
        }

        targetAlpha = targetAlpha.copy(next = target)
        if (targetAlpha.current != target) {
            animate(
                initialValue = targetAlpha.alpha,
                targetValue = 0f,
                initialVelocity = velocity,
                animationSpec = tween(
                    durationMillis = durationMillis / 2,
                    easing = FastOutLinearInEasing,
                ),
            ) { newAlpha, newVelocity ->
                targetAlpha = targetAlpha.copy(alpha = newAlpha)
                velocity = newVelocity
            }
            velocity = -velocity
            targetAlpha = targetAlpha.copy(current = target, alpha = 0f)
        }

        animate(
            initialValue = targetAlpha.alpha,
            targetValue = 1f,
            initialVelocity = velocity,
            animationSpec = tween(
                durationMillis = durationMillis / 2,
                easing = LinearOutSlowInEasing,
            ),
        ) { newAlpha, newVelocity ->
            targetAlpha = targetAlpha.copy(alpha = newAlpha)
            velocity = newVelocity
        }
        velocity = 0f
    }
    return targetAlphaState
}
