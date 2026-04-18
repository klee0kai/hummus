package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: ButtonColors,
): State<ButtonColors> {
    val containerColor = animateColorAsState(colors.containerColor)
    val contentColor = animateColorAsState(colors.contentColor)
    val disabledContainerColor = animateColorAsState(colors.disabledContainerColor)
    val disabledContentColor = animateColorAsState(colors.disabledContentColor)

    return rememberDerivedStateOf {
        ButtonColors(
            containerColor = containerColor.value,
            contentColor = contentColor.value,
            disabledContainerColor = disabledContainerColor.value,
            disabledContentColor = disabledContentColor.value,
        )
    }
}

@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: ColorScheme,
): State<ColorScheme> {
    val primary = animateColorAsState(colors.primary)
    val onPrimary = animateColorAsState(colors.onPrimary)
    val primaryContainer = animateColorAsState(colors.primaryContainer)
    val onPrimaryContainer = animateColorAsState(colors.onPrimaryContainer)
    val inversePrimary = animateColorAsState(colors.inversePrimary)
    val secondary = animateColorAsState(colors.secondary)
    val onSecondary = animateColorAsState(colors.onSecondary)
    val secondaryContainer = animateColorAsState(colors.secondaryContainer)
    val onSecondaryContainer = animateColorAsState(colors.onSecondaryContainer)
    val tertiary = animateColorAsState(colors.tertiary)
    val onTertiary = animateColorAsState(colors.onTertiary)
    val tertiaryContainer = animateColorAsState(colors.tertiaryContainer)
    val onTertiaryContainer = animateColorAsState(colors.onTertiaryContainer)
    val background = animateColorAsState(colors.background)
    val onBackground = animateColorAsState(colors.onBackground)
    val surface = animateColorAsState(colors.surface)
    val onSurface = animateColorAsState(colors.onSurface)
    val surfaceVariant = animateColorAsState(colors.surfaceVariant)
    val onSurfaceVariant = animateColorAsState(colors.onSurfaceVariant)
    val surfaceTint = animateColorAsState(colors.surfaceTint)
    val inverseSurface = animateColorAsState(colors.inverseSurface)
    val inverseOnSurface = animateColorAsState(colors.inverseOnSurface)
    val error = animateColorAsState(colors.error)
    val onError = animateColorAsState(colors.onError)
    val errorContainer = animateColorAsState(colors.errorContainer)
    val onErrorContainer = animateColorAsState(colors.onErrorContainer)
    val outline = animateColorAsState(colors.outline)
    val outlineVariant = animateColorAsState(colors.outlineVariant)
    val scrim = animateColorAsState(colors.scrim)
    val surfaceBright = animateColorAsState(colors.surfaceBright)
    val surfaceDim = animateColorAsState(colors.surfaceDim)
    val surfaceContainer = animateColorAsState(colors.surfaceContainer)
    val surfaceContainerHigh = animateColorAsState(colors.surfaceContainerHigh)
    val surfaceContainerHighest = animateColorAsState(colors.surfaceContainerHighest)
    val surfaceContainerLow = animateColorAsState(colors.surfaceContainerLow)
    val surfaceContainerLowest = animateColorAsState(colors.surfaceContainerLowest)

    return rememberDerivedStateOf {
        ColorScheme(
            primary = primary.value,
            onPrimary = onPrimary.value,
            primaryContainer = primaryContainer.value,
            onPrimaryContainer = onPrimaryContainer.value,
            inversePrimary = inversePrimary.value,
            secondary = secondary.value,
            onSecondary = onSecondary.value,
            secondaryContainer = secondaryContainer.value,
            onSecondaryContainer = onSecondaryContainer.value,
            tertiary = tertiary.value,
            onTertiary = onTertiary.value,
            tertiaryContainer = tertiaryContainer.value,
            onTertiaryContainer = onTertiaryContainer.value,
            background = background.value,
            onBackground = onBackground.value,
            surface = surface.value,
            onSurface = onSurface.value,
            surfaceVariant = surfaceVariant.value,
            onSurfaceVariant = onSurfaceVariant.value,
            surfaceTint = surfaceTint.value,
            inverseSurface = inverseSurface.value,
            inverseOnSurface = inverseOnSurface.value,
            error = error.value,
            onError = onError.value,
            errorContainer = errorContainer.value,
            onErrorContainer = onErrorContainer.value,
            outline = outline.value,
            outlineVariant = outlineVariant.value,
            scrim = scrim.value,
            surfaceBright = surfaceBright.value,
            surfaceDim = surfaceDim.value,
            surfaceContainer = surfaceContainer.value,
            surfaceContainerHigh = surfaceContainerHigh.value,
            surfaceContainerHighest = surfaceContainerHighest.value,
            surfaceContainerLow = surfaceContainerLow.value,
            surfaceContainerLowest = surfaceContainerLowest.value,
        )
    }
}