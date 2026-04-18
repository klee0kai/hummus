package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Stable
data class NavigationBoardColors(
    val headerBackgroundColor: Color,
    val bodyBackgroundColor: Color,
)


@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: NavigationBoardColors,
): State<NavigationBoardColors> {
    val headerBackgroundColor = animateColorAsState(
        targetValue = colors.headerBackgroundColor,
        label = ""
    )
    val bodyBackgroundColor = animateColorAsState(
        targetValue = colors.bodyBackgroundColor,
        label = ""
    )

    return rememberDerivedStateOf {
        NavigationBoardColors(
            headerBackgroundColor = headerBackgroundColor.value,
            bodyBackgroundColor = bodyBackgroundColor.value,
        )
    }
}