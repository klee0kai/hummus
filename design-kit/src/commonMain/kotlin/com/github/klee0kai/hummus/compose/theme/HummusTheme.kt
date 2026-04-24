package com.github.klee0kai.hummus.compose.theme

import androidx.compose.material3.Shapes
import androidx.compose.runtime.Stable
import com.github.klee0kai.hummus.compose.theme.color.HummusColorScheme
import com.github.klee0kai.hummus.compose.theme.typography.TypeScheme
import kotlinx.serialization.Serializable

@Stable
data class HummusTheme(
    val colorScheme: HummusColorScheme,
    val typeScheme: TypeScheme,
    val shapes: Shapes,
)