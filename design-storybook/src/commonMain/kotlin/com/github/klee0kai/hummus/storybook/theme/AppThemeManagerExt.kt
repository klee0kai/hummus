package com.github.klee0kai.hummus.storybook.theme

import androidx.compose.ui.graphics.Color


fun AppThemeManager.modifyTransparent(transparent: Boolean) {
    modify("autofill_transparent") {
        if (transparent) {
            copy(colorScheme = colorScheme.copy(windowBackgroundColor = Color.Transparent))
        } else {
            this
        }
    }
}