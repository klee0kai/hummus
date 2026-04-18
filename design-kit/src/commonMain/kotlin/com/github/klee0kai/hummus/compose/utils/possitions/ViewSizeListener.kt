package com.github.klee0kai.hummus.compose.utils.possitions

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt

@Composable
@NonRestartableComposable
fun rememberViewPosition() = remember { mutableStateOf<ViewPositionPx?>(null) }

fun Modifier.onGlobalPositionState(
    onChange: (ViewPositionPx) -> Unit
) = onGloballyPositioned { layoutCoordinates ->
    if (layoutCoordinates.isAttached) {
        val state = ViewPositionPx(
            globalPos = layoutCoordinates
                .positionInRoot()
                .run { IntOffset(x.roundToInt(), y.roundToInt()) },
            size = layoutCoordinates.size
        )
        onChange.invoke(state)
    }
}

fun Modifier.onGlobalPositionState(state: MutableState<in ViewPositionPx>) =
    onGlobalPositionState { state.value = it }

fun Modifier.placeTo(viewPositionDp: ViewPositionDp) = this
    .size(width = viewPositionDp.size.width, height = viewPositionDp.size.height)
    .absoluteOffset(x = viewPositionDp.globalPos.x, y = viewPositionDp.globalPos.y)


@Composable
fun ViewPositionPx.toDp() = toDp(LocalDensity.current)

fun ViewPositionPx.toDp(density: Density) = with(density) {
    ViewPositionDp(
        globalPos = DpOffset(globalPos.x.toDp(), globalPos.y.toDp()),
        size = DpSize(size.width.toDp(), size.height.toDp())
    )
}

@Composable
fun IntSize.pxToDp() = with(LocalDensity.current) {
    DpSize(width.toDp(), height.toDp())
}

fun IntSize.pxToDp(density: Density) = with(density) {
    DpSize(width.toDp(), height.toDp())
}

@Composable
fun Float.pxToDp(): Dp {
    val px = this
    return with(LocalDensity.current) { px.toDp() }
}

@Composable
fun Int.pxToDp(): Dp {
    val px = this
    return with(LocalDensity.current) { px.toDp() }
}

@Composable
fun Dp.toPx(): Float {
    val dp = this
    return with(LocalDensity.current) { dp.toPx() }
}
