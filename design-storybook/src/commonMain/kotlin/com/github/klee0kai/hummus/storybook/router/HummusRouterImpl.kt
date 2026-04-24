@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.github.klee0kai.hummus.storybook.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TypeSpecimen
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.navigation.HummusRouter
import com.github.klee0kai.hummus.compose.navigation.NavItem
import com.github.klee0kai.hummus.coroutine.lazyStateFlow
import com.github.klee0kai.hummus.coroutine.touch
import com.github.klee0kai.hummus.storybook.di.StoryBookDI
import com.github.klee0kai.hummus.storybook.navigation.ColorSchemeBrowserDestination
import com.github.klee0kai.hummus.storybook.navigation.ComponentsBrowserDestination
import com.github.klee0kai.hummus.storybook.navigation.TypographyBrowserDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HummusRouterImpl : HummusRouter {

    val scope = StoryBookDI.mainThreadScope()
    val dispatcher = StoryBookDI.mainDispatcher()
    val backHandles = mutableListOf<() -> Unit>()

    val screenResolver by lazy { StoryBookDI.screenResolver() }

    override val navItems = lazyStateFlow(
        init = emptyList<NavItem>(),
        scope = scope,
        defaultArg = Unit,
    ) {
        value = buildList {
            add(
                NavItem(
                    icon = Icons.Default.Apps,
                    text = "components",
                    destination = ComponentsBrowserDestination,
                )
            )
            add(
                NavItem(
                    icon = Icons.Default.Palette,
                    text = "Colors",
                    destination = ColorSchemeBrowserDestination,
                )
            )
            add(
                NavItem(
                    icon = Icons.Default.TypeSpecimen,
                    text = "Typography",
                    destination = TypographyBrowserDestination,
                )
            )
        }
    }

    override var navigator: ThreePaneScaffoldNavigator<Destination>? = null

    override fun navigator(
        navigator: ThreePaneScaffoldNavigator<Destination>?,
    ): ThreePaneScaffoldNavigator<Destination> {
        if (navigator != null) {
            this.navigator = navigator
        }
        return this.navigator!!
    }

    override fun navigate(
        destination: Destination,
    ) = scope.launch {
        val screenPanel = screenResolver.screenOf(destination) ?: return@launch
        when {
            screenPanel.extras != null -> {
                navigator?.navigateTo(
                    pane = ListDetailPaneScaffoldRole.Extra,
                    contentKey = destination,
                )
            }

            screenPanel.details != null -> {
                navigator?.navigateTo(
                    pane = ListDetailPaneScaffoldRole.Detail,
                    contentKey = destination,
                )
            }

            else -> {
                navigator?.navigateTo(
                    pane = ListDetailPaneScaffoldRole.List,
                    contentKey = destination,
                )
            }
        }
    }

    override fun resetStack(
        vararg destinations: Destination
    ): Job = scope.launch {
        while (navigator?.canNavigateBack() == true) {
            navigator?.navigateBack()
        }
        destinations.forEach { destination ->
            navigate(destination)
        }
        navItems.touch()
    }


    override fun selectedNavItem(
        item: Destination,
    ) = scope.launch {
        navigate(item)
    }

    override fun back(
    ): Job = scope.launch {
        val handler = backHandles.firstOrNull()
        if (handler != null) {
            handler.invoke()
            return@launch
        }

        if (navigator?.canNavigateBack() == true) {
            navigator?.navigateBack()
        }
    }

    @Composable
    override fun BackHandler(
        isEnabled: Boolean,
        onBack: () -> Unit,
    ) {
        DisposableEffect(isEnabled) {
            backHandles.add(0, onBack)

            onDispose {
                backHandles -= onBack
            }
        }
    }

}