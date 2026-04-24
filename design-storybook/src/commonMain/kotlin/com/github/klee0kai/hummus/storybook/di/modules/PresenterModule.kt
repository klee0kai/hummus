package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.hummus.storybook.ui.main.viewmodel.AppBarViewModel
import com.github.klee0kai.hummus.storybook.ui.main.viewmodel.AppBarViewModelImpl
import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide

@Module
interface PresenterModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun appBarViewModel(): AppBarViewModel = AppBarViewModelImpl()

}