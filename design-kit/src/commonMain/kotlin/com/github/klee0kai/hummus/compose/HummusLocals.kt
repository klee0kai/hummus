package com.github.klee0kai.hummus.compose

import androidx.compose.runtime.compositionLocalOf
import com.github.klee0kai.hummus.compose.navigation.HummusRouter
import com.github.klee0kai.hummus.compose.theme.HummusTheme


val LocalHummusRouter = compositionLocalOf<HummusRouter> { error("no router") }
val LocalHummusTheme = compositionLocalOf<HummusTheme> { error("no theme") }
val LocalHummusComposeConfig = compositionLocalOf<HummusComposeConfig> { HummusComposeConfig() }