package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Stable
data class SurfaceScheme(
    val surfaceColor: Color,
    val onSurfaceColor: Color,
)

@Stable
data class SurfaceSchemas(
    val noColor: SurfaceScheme,
    val violet: SurfaceScheme,
    val turquoise: SurfaceScheme,
    val pink: SurfaceScheme,
    val orange: SurfaceScheme,
    val coral: SurfaceScheme,
) {

    val colorsGroupCollection: List<SurfaceScheme> = listOf(
        violet, turquoise, pink, orange, coral,
    )
}




@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: SurfaceScheme,
): State<SurfaceScheme> {
    val surfaceColor = animateColorAsState(
        targetValue = colors.surfaceColor,
        label = ""
    )
    val onSurfaceColor = animateColorAsState(
        targetValue = colors.onSurfaceColor,
        label = ""
    )

    return rememberDerivedStateOf {
        SurfaceScheme(
            surfaceColor = surfaceColor.value,
            onSurfaceColor = onSurfaceColor.value,
        )
    }
}


@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: SurfaceSchemas,
): State<SurfaceSchemas> {
    val noColor = animateColorAsState(colors.noColor)
    val violet = animateColorAsState(colors.violet)
    val turquoise = animateColorAsState(colors.turquoise)
    val pink = animateColorAsState(colors.pink)
    val orange = animateColorAsState(colors.orange)
    val coral = animateColorAsState(colors.coral)

    return rememberDerivedStateOf {
        SurfaceSchemas(
            noColor = noColor.value,
            violet = violet.value,
            turquoise = turquoise.value,
            pink = pink.value,
            orange = orange.value,
            coral = coral.value,
        )
    }
}