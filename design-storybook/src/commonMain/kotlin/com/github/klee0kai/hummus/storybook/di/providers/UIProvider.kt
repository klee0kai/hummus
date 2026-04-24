package com.github.klee0kai.hummus.storybook.di.providers

import com.github.klee0kai.hummus.compose.navigation.HummusRouter
import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolver
import com.github.klee0kai.hummus.storybook.theme.AppThemeManager

interface UIProvider {

    fun appThemeManager(): AppThemeManager

    fun appRouter(): HummusRouter

    fun screenResolver(): ScreenResolver

}