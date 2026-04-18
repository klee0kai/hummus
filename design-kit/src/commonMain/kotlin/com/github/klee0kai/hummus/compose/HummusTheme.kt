package com.github.klee0kai.hummus.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes
import com.github.klee0kai.hummus.compose.theme.HummusTheme
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme

@Composable
fun HummusTheme(
    modifier: Modifier = Modifier,
    theme: HummusTheme = HummusDefaultThemes.darkTheme,
    hummusComposeConfig: HummusComposeConfig? = null,
    content: @Composable () -> Unit,
) {

    val isEditMode = LocalInspectionMode.current || isDebugInspectorInfoEnabled
    val shimmer = remember {
        defaultShimmerTheme.copy(
            shaderColors = listOf(
                Color.Unspecified.copy(alpha = .25f),
                Color.Unspecified.copy(alpha = .4f),
                Color.Unspecified.copy(alpha = .25f),
            ),
        )
    }

    CompositionLocalProvider(
        LocalHummusTheme provides theme,
        LocalShimmerTheme provides shimmer,
    ) {
        MaterialTheme(
            colorScheme = theme.colorScheme.androidColorScheme,
            typography = theme.typeScheme.typography,
            shapes = theme.shapes,
        ) {
            content.invoke()
        }
    }
}