package com.github.klee0kai.hummus.storybook.di

import com.github.klee0kai.hummus.compose.HummusComposeConfig
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.storybook.di.modules.ComposeModules
import com.github.klee0kai.hummus.storybook.di.modules.UIComposeModule
import com.github.klee0kai.hummus.storybook.di.providers.ComposeProviders
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.module.BindInstance

fun initStoryBookComponent(): StoryBookComponent = StoryBookComponentStoneComponent()

var StoryBookDI: StoryBookComponent = initStoryBookComponent()
    private set

@Component(
    identifiers = [
    ],

    )
interface StoryBookComponent : ComposeProviders, ComposeModules {

    @BindInstance(cache = BindInstance.CacheType.Strong)
    fun config(newConfig: HummusComposeConfig? = null): HummusComposeConfig

}


@DebugOnly
fun StoryBookComponent.hardResetToPreview() {
//    StoryBookDI = initComposeComponent()

    initDummyModule()
}

@DebugOnly
fun StoryBookComponent.initDummyModule() {
    initUiComposeModule(object : UIComposeModule {

    })

}

