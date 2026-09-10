package com.github.klee0kai.hummus.storybook.theme

import com.github.klee0kai.hummus.compose.theme.HummusTheme
import com.github.klee0kai.hummus.compose.theme.ThemeIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface AppThemeManager {

    val theme: Flow<HummusTheme> get() = emptyFlow()

    val themeIdentifier: Flow<ThemeIdentifier> get() = emptyFlow()

    /**
     * set theme
     */
    fun setTheme(themeIdentifier: ThemeIdentifier) = Unit

    /**
     * when changing the theme, theme modifiers are applied by their identifiers
     */
    fun modify(
        id: String,
        modifier: HummusTheme.() -> HummusTheme = { this },
    ) = Unit

    /**
     * transform theme identifier to hummus theme
     */
    fun ThemeIdentifier.toTheme(): HummusTheme
}