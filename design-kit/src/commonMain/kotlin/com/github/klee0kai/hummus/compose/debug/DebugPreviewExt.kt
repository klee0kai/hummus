package com.github.klee0kai.hummus.compose.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.LocalHummusComposeConfig
import com.github.klee0kai.hummus.compose.components.overlay.OverlayContainer
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.theme.HummusTheme


@DebugOnly
@Composable
fun DebugScreenPreview(
    layoutDirection: LayoutDirection? = null,
    theme: HummusTheme? = null,
    content: @Composable () -> Unit
) {
    var flags = LocalHummusComposeConfig.current
    if (layoutDirection != null) {
        flags = flags.copy(layoutDirection = layoutDirection)
    }
    if (theme != null) {
        flags = flags.copy(theme = theme)
    }

    CompositionLocalProvider(
        LocalHummusComposeConfig provides flags
    ) {
        if (flags.isOverrideInsets) {
//            EdgeToEdgeTemplate {
            DebugContentPreview {
                content()
            }
//            }
        } else {
            DebugContentPreview {
                content()
            }
        }
    }
}

@DebugOnly
@Composable
fun DebugContentPreview(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    theme: HummusTheme? = null,
    content: @Composable () -> Unit
) {
    var flags = LocalHummusComposeConfig.current
    if (layoutDirection != null) {
        flags = flags.copy(layoutDirection = layoutDirection)
    }
    if (theme != null) {
        flags = flags.copy(theme = theme)
    }

    CompositionLocalProvider(
        LocalHummusComposeConfig provides flags
    ) {
        HummusTheme(
//            theme = previewFlags.theme,
        ) {
            OverlayContainer {
                content()
            }
        }
    }
}