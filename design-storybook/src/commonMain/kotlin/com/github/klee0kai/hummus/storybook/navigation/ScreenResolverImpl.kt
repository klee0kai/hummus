package com.github.klee0kai.hummus.storybook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.navigation.WidgetState
import com.github.klee0kai.hummus.compose.screenresolver.ScreenPanel
import com.github.klee0kai.hummus.compose.screenresolver.ScreenResolver
import com.github.klee0kai.hummus.storybook.ui.color_screen.ColorSchemeBrowserScreen
import com.github.klee0kai.hummus.storybook.ui.components_screen.ComponentEditScreen
import com.github.klee0kai.hummus.storybook.ui.components_screen.ComponentListScreen
import com.github.klee0kai.hummus.storybook.ui.typography_screen.TypographyBrowserScreen

open class ScreenResolverImpl : ScreenResolver {

    override fun screenOf(
        destination: Destination,
    ): ScreenPanel? = when (destination) {
        is ComponentsBrowserDestination -> {
            ScreenPanel(list = { ComponentListScreen() })
        }

        is ComponentEditDestination -> {
            ScreenPanel(
                list = { ComponentListScreen(selectedComponent = destination.component) },
                details = { ComponentEditScreen(destination) }
            )
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