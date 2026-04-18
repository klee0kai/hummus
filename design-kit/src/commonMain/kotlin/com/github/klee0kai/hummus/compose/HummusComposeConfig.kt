package com.github.klee0kai.hummus.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.LayoutDirection
import com.github.klee0kai.hummus.compose.theme.HummusTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Stable
data class HummusComposeConfig(
    val isDebug: Boolean = false,
    val isOverrideInsets: Boolean = true,
    val isViewEditMode: Boolean = false,
    val isSetTitleAvailable: Boolean = true,
    val dialogForceVisible: Boolean = true,
    val theme: HummusTheme? = null,
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    val warmUpTimeDefault: Duration = 300.milliseconds,
)