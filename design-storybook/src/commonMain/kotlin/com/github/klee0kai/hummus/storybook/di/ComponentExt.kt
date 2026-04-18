package com.github.klee0kai.hummus.storybook.di

import com.github.klee0kai.hummus.compose.HummusComposeConfig

fun StoryBookComponent.updateConfig(
    block: HummusComposeConfig.() -> HummusComposeConfig
) {
    config()
        .block()
        .let {
            config(it)
        }
}