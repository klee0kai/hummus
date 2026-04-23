@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.hummus.storybook

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarStates
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.utils.views.collectAsState
import com.github.klee0kai.hummus.storybook.di.StoryBookDI
import com.github.klee0kai.hummus.storybook.navigation.MainDestinations

@Composable
fun NavContainer() {
    val router = remember { StoryBookDI.appRouter() }
    val navigator = rememberListDetailPaneScaffoldNavigator<Destination>()
    router.navigator(navigator)

    val screenResolver = remember { StoryBookDI.screenResolver() }

    val navItems by router.navItems.collectAsState(key = Unit, emptyList())
    val selectedItem = navigator.currentDestination?.contentKey ?: MainDestinations.InitDest
    val directive = navigator.scaffoldDirective
    val scaffoldValue = navigator.scaffoldValue

    CompositionLocalProvider(
        LocalHummusRouter provides router
    ) {
        HummusTheme {
            HummusBarStates()

            NavigationSuiteScaffold(
                modifier = Modifier.fillMaxSize()
                    .padding(top = 64.dp),
                navigationSuiteItems = {
                    navItems.forEach { item ->
                        item(
                            icon = { Icon(item.icon, item.text) },
                            label = { Text(item.text) },
                            selected = selectedItem == item.destination,
                            onClick = { router.selectedNavItem(item.destination) }
                        )
                    }
                }
            ) {

                ListDetailPaneScaffold(
                    directive = directive,
                    value = scaffoldValue,
                    listPane = {
                        screenResolver.screenOf(selectedItem)?.list?.let { screen ->
                            AnimatedPane {
                                screen.invoke()
                            }
                        }
                    },

                    detailPane = {
                        screenResolver.screenOf(selectedItem)?.details?.let { screen ->
                            AnimatedPane {
                                screen.invoke()
                            }
                        }
                    },
                    extraPane = {
                        screenResolver.screenOf(selectedItem)?.extras?.let { screen ->
                            AnimatedPane {
                                screen.invoke()
                            }
                        }
                    }
                )

            }
        }
    }

}