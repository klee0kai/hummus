package com.github.klee0kai.hummus.storybook.navigation

import com.github.klee0kai.hummus.compose.navigation.Destination

object MainDestinations {
    val InitDest: Destination = ComponentsBrowserDestination
}


data object ComponentsBrowserDestination : Destination

data object ColorSchemeBrowserDestination : Destination

data object TypographyBrowserDestination : Destination
