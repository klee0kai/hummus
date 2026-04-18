package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory

interface ComposeModules {

    /* get module */
    fun uiComposeModule(): UIComposeModule
    fun coroutineModule(): CoroutineModule


    /* get origin factories */
    @ModuleOriginFactory
    fun uiComposeModuleFactory(): UIComposeModule

    @ModuleOriginFactory
    fun coroutineModuleFactory(): CoroutineModule

    /* override */

    @Init
    fun initUiComposeModule(uiModule: UIComposeModule)

    @Init
    fun initCoroutineModule(coroutine: CoroutineModule)

}