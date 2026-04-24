package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Stable
data class PopupMenuColors(
    val surfaceColor: Color,
    val contentColor: Color,
    val shadowColor: Color,
)


@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: PopupMenuColors,
): State<PopupMenuColors> {
    val surfaceColor = animateColorAsState(
        targetValue = colors.surfaceColor,
        label = ""
    )

    val contentColor = animateColorAsState(
        targetValue = colors.contentColor,
        label = ""
    )

    val shadowColor = animateColorAsState(
        targetValue = colors.shadowColor,
        label = ""
    )


    return rememberDerivedStateOf {
        PopupMenuColors(
            surfaceColor = surfaceColor.value,
            contentColor = contentColor.value,
            shadowColor = shadowColor.value,
        )
    }
}