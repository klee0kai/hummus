package com.github.klee0kai.hummus.compose.screenresolver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.navigation.WidgetState

interface ScreenResolver {

    fun screenOf(
        destination: Destination,
    ): ScreenPanel?

    @Composable
    fun widget(
        modifier: Modifier,
        widgetState: WidgetState,
    ) = Unit


}