package com.github.klee0kai.hummus.storybook.navigation

import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.design.core.DesignComponentMethod

object MainDestinations {
    val InitDest: Destination = ComponentsBrowserDestination
}


data object ComponentsBrowserDestination : Destination

data class ComponentEditDestination(
    val component: DesignComponentMethod? = null,
) : Destination

data object ColorSchemeBrowserDestination : Destination

data object TypographyBrowserDestination : Destination
