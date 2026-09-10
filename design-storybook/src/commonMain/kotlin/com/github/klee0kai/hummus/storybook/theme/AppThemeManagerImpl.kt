package com.github.klee0kai.hummus.storybook.theme

import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes
import com.github.klee0kai.hummus.compose.theme.HummusTheme
import com.github.klee0kai.hummus.compose.theme.ThemeIdentifier
import com.github.klee0kai.hummus.coroutine.launchSafe
import com.github.klee0kai.hummus.coroutine.lazyStateFlow
import com.github.klee0kai.hummus.storybook.di.StoryBookDI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.withLock

open class AppThemeManagerImpl(
    val defThemeIdentifier: ThemeIdentifier = ThemeIdentifier.LightTheme
) : AppThemeManager {

    private val scope = StoryBookDI.defaultThreadScope()

    private val modifiers = mutableListOf<Pair<String, HummusTheme.() -> HummusTheme>>()

    override val themeIdentifier = MutableStateFlow<ThemeIdentifier>(defThemeIdentifier)

    override val theme = lazyStateFlow(
        init = defThemeIdentifier.toTheme(),
        scope = scope,
        defaultArg = Unit
    ) {
        themeIdentifier.collect { identifier ->
            var theme = identifier.toTheme()
            scope.mutex.withLock {
                modifiers.forEach { (_, modifier) ->
                    theme = modifier.invoke(theme)
                }
            }
            value = theme
        }

    }

    override fun setTheme(themeIdentifier: ThemeIdentifier) {
        this@AppThemeManagerImpl.themeIdentifier.value = themeIdentifier
    }

    override fun modify(id: String, modifier: HummusTheme.() -> HummusTheme) {
        scope.launchSafe {
            modifiers.removeAll { it.first == id }
            modifiers.add(id to modifier)
            theme.touch(Unit)
        }
    }

    override fun ThemeIdentifier.toTheme(): HummusTheme = when (this) {
        ThemeIdentifier.DarkTheme -> HummusDefaultThemes.darkTheme
        ThemeIdentifier.LightTheme -> HummusDefaultThemes.lightTheme
    }

}

