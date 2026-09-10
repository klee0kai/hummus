package com.github.klee0kai.hummus.compose.theme

import androidx.compose.material3.Shapes
import com.github.klee0kai.hummus.compose.theme.color.darkCommonColorScheme
import com.github.klee0kai.hummus.compose.theme.color.lightCommonColorScheme
import com.github.klee0kai.hummus.compose.theme.typography.regularAppTypeScheme

object HummusDefaultThemes {

    val darkTheme = HummusTheme(
        colorScheme = darkCommonColorScheme(),
        typeScheme = regularAppTypeScheme(),
        shapes = Shapes(),
    )

    val lightTheme = HummusTheme(
        colorScheme = lightCommonColorScheme(),
        typeScheme = regularAppTypeScheme(),
        shapes = Shapes(),
    )


}