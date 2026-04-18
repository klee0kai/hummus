package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolver
import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolverCommonImpl
import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide

@Module(genProviderName = "UIProvider")
interface UIComposeModule {

//    @Provide(cache = Provide.CacheType.Weak)
//    fun appThemeManager(): AppThemeManager = AppThemeManagerImpl()

//    @Provide(cache = Provide.CacheType.Weak)
//    fun appRouter(): HummusRouter = HummusRouterImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun screenResolver(): ScreenResolver = ScreenResolverCommonImpl()

}