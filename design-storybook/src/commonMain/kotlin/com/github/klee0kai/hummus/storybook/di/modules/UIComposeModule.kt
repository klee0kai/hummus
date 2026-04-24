package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.hummus.compose.navigation.HummusRouter
import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolver
import com.github.klee0kai.hummus.storybook.navigation.ScreenResolverImpl
import com.github.klee0kai.hummus.storybook.router.HummusRouterImpl
import com.github.klee0kai.hummus.storybook.theme.AppThemeManager
import com.github.klee0kai.hummus.storybook.theme.AppThemeManagerImpl
import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide

@Module
interface UIComposeModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun appThemeManager(): AppThemeManager = AppThemeManagerImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun appRouter(): HummusRouter = HummusRouterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun screenResolver(): ScreenResolver = ScreenResolverImpl()

}