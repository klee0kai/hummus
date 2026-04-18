package com.github.klee0kai.hummus.compose.screenresolver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.navigation.Destination
import com.github.klee0kai.hummus.compose.navigation.WidgetState

open class ScreenResolverCommonImpl : ScreenResolver {

    override fun screenOf(
        destination: Destination,
    ): ScreenPanel? = when (destination) {
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
