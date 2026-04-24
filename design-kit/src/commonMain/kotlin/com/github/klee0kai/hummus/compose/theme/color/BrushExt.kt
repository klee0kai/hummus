package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Brush.Companion.bottomShadow(
    backgroundColor: Color,
    offset: Float = 0.3f,
) = verticalGradient(
    0f to Color.Transparent,
    offset to backgroundColor,
    1f to backgroundColor,
)
