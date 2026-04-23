package com.github.klee0kai.hummus.storybook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.navigation.WidgetState
import com.github.klee0kai.hummus.compose.screenresolver.ScreenPanel
import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolver
import com.github.klee0kai.hummus.storybook.storybook.screens.ColorSchemeBrowserScreen
import com.github.klee0kai.hummus.storybook.storybook.screens.DesignComponentsBrowser
import com.github.klee0kai.hummus.storybook.storybook.screens.TypographyBrowserScreen

open class ScreenResolverImpl : ScreenResolver {

    override fun screenOf(
        destination: Destination,
    ): ScreenPanel? = when (destination) {
        is ComponentsBrowserDestination -> {
            ScreenPanel(list = { DesignComponentsBrowser() })
        }

        is ColorSchemeBrowserDestination -> {
            ScreenPanel(list = { ColorSchemeBrowserScreen() })
        }

        is TypographyBrowserDestination -> {
            ScreenPanel(list = { TypographyBrowserScreen() })
        }

        else -> null
    }

    @Composable
    override fun widget(
        modifier: Modifier,
        widgetState: WidgetState
    ) {
        // common widgets
    }


}