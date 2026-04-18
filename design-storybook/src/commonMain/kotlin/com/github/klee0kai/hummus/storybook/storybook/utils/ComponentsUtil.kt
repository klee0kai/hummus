package com.github.klee0kai.hummus.storybook.storybook.utils

import com.github.klee0kai.hummus.design.core.DesignComponentMethod

/**
 * Получить список всех зарегистрированных компонентов
 */
fun getRegisteredComponents(): List<DesignComponentMethod> {
    return try {
        com.github.klee0kai.hummus.compose.components.DesignComponentsRegistry.components
    } catch (e: Exception) {
        emptyList()
    }
}
