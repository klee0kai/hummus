package com.github.klee0kai.hummus.compose.theme


sealed interface ThemeIdentifier {

    /**
     * Dark theme realization
     */
    data object DarkTheme : ThemeIdentifier

    /**
     * Light theme realization
     */
    data object LightTheme : ThemeIdentifier

}


fun ThemeIdentifier.next(): ThemeIdentifier = when (this) {
    ThemeIdentifier.DarkTheme -> ThemeIdentifier.LightTheme
    ThemeIdentifier.LightTheme -> ThemeIdentifier.DarkTheme
}