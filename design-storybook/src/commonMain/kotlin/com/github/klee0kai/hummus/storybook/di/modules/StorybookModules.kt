package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory

interface StorybookModules {

    /* get module */
    fun uiComposeModule(): UIComposeModule
    fun presenterModule(): PresenterModule
    fun coroutineModule(): CoroutineModule


    /* get origin factories */
    @ModuleOriginFactory
    fun uiComposeModuleFactory(): UIComposeModule

    @ModuleOriginFactory
    fun presenterModuleFactory(): PresenterModule

    @ModuleOriginFactory
    fun coroutineModuleFactory(): CoroutineModule

    /* override */

    @Init
    fun initUiComposeModule(uiModule: UIComposeModule)

    @Init
    fun initPresenterModule(presenterModule: PresenterModule)

    @Init
    fun initCoroutineModule(coroutine: CoroutineModule)

}