package com.github.klee0kai.hummus.storybook.storybook.utils

import com.github.klee0kai.hummus.compose.components.DesignComponentsRegistry
import com.github.klee0kai.hummus.design.core.DesignComponentMethod

actual fun getRegisteredDesignComponents(
): List<DesignComponentMethod> = DesignComponentsRegistry.components
