package com.github.klee0kai.hummus.storybook.di.providers

import com.github.klee0kai.hummus.storybook.di.modules.CoroutineProvider
import com.github.klee0kai.hummus.storybook.di.modules.UIProvider
import com.github.klee0kai.stone.annotations.dependencies.Dependencies

@Dependencies
interface ComposeProviders :
    UIProvider,
    CoroutineProvider