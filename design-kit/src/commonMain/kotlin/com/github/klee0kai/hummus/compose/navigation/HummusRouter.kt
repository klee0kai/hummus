@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.github.klee0kai.hummus.compose.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import com.github.klee0kai.hummus.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface HummusRouter {

    val navItems: Flow<List<NavItem>> get() = emptyFlow()

    val navigator: ThreePaneScaffoldNavigator<Destination>? get() = null

    fun navigator(
        navigator: ThreePaneScaffoldNavigator<Destination>? = null,
    ): ThreePaneScaffoldNavigator<Destination> = error("no navigator")

    fun navigate(destination: Destination): Job = Job()

    fun resetStack(vararg destinations: Destination): Job = emptyJob()

    fun selectedNavItem(item: Destination): Job = Job()

    fun back(): Job = Job()

    fun hideKeyboard(): Job = Job()

    @Composable
    fun BackHandler(
        isEnabled: Boolean = true,
        onBack: () -> Unit,
    ) = Unit

}