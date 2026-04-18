@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.hummus.compose.components.appbar

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.debug.DebugContentPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.utils.views.animateAlphaAsState
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

object HummusBarConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
}

@Composable
fun HummusBarStates(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable () -> Unit)? = null,
) {
    val theme = LocalHummusTheme.current
    val appBarAlpha by animateAlphaAsState(isVisible)
    val isNotVisible by rememberDerivedStateOf { appBarAlpha <= 0 }

    if (isNotVisible) return
    CenterAlignedTopAppBar(
        modifier = modifier
            .alpha(appBarAlpha),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        actions = actions,
        title = {
            CompositionLocalProvider(
                LocalTextStyle provides theme.typeScheme.screenHeader,
            ) {
                Box(
                    modifier = modifier
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    titleContent?.invoke()
                }
            }
        },
        navigationIcon = navigationIcon ?: {},
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun AppBarTitlePreview1() = DebugContentPreview {
    HummusBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }
        }
    ) {
        Text("Title")
    }
}