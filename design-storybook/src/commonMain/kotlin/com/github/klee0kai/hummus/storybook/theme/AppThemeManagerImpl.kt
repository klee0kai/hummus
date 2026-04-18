//package com.github.klee0kai.hummus.storybook.theme
//
//import com.github.klee0kai.cloud.core.di.CoreDI
//import com.github.klee0kai.cloud.core.utils.coroutine.launchSafe
//import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes.defThemeIdentifier
//import com.github.klee0kai.cloud.utils.coroutine.touchable
//import com.github.klee0kai.hummus.compose.theme.HummusTheme
//import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes
//import com.github.klee0kai.hummus.compose.theme.ThemeIdentifier
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.sync.withLock
//
//class AppThemeManagerImpl : AppThemeManager {
//
//    private val scope = CoreDI.defaultThreadScope()
//
//    private val modifiers = mutableListOf<Pair<String, HummusTheme.() -> HummusTheme>>()
//
//    override val themeIdentifier = MutableStateFlow<ThemeIdentifier>(defThemeIdentifier)
//
//    override val theme = themeIdentifier.map {
//        var theme = it.toTheme()
//        scope.mutex.withLock {
//            modifiers.forEach { (_, modifier) ->
//                theme = modifier.invoke(theme)
//            }
//        }
//        theme
//    }.flowOn(CoreDI.defaultDispatcher())
//        .touchable()
//
//
//    override fun setTheme(themeIdentifier: ThemeIdentifier) {
//        this@AppThemeManagerImpl.themeIdentifier.value = themeIdentifier
//    }
//
//    override fun modify(id: String, modifier: HummusTheme.() -> HummusTheme) {
//        scope.launchSafe {
//            modifiers.removeAll { it.first == id }
//            modifiers.add(id to modifier)
//            theme.touch(Unit)
//        }
//    }
//
//}
//
//fun ThemeIdentifier.toTheme(): HummusTheme = when (this) {
//    ThemeIdentifier.DarkTheme -> HummusDefaultThemes.darkTheme
//    ThemeIdentifier.LightTheme -> HummusDefaultThemes.lightTheme
//}