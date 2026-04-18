package com.github.klee0kai.hummus.storybook.previews

import androidx.annotation.VisibleForTesting
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.desktop.ui.tooling.preview.Preview
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarStates
import com.github.klee0kai.hummus.compose.components.appbar.LoadingTitle
import com.github.klee0kai.hummus.compose.components.appbar.SearchTitle
import com.github.klee0kai.hummus.compose.debug.DebugContentPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly


@OptIn(DebugOnly::class)
@Preview
@Composable
fun SearchFieldEmptyPreview() = DebugContentPreview {
    HummusBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        SearchTitle(
            textModifier = Modifier,
            searchTitle = "Search",
            searchText = "",
        )
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun SearchFieldTextPreview() = DebugContentPreview {
    HummusBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        SearchTitle(
            textModifier = Modifier,
            searchTitle = "Search",
            searchText = "some text",
        )
    }
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

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun StorageLoadingTitlePreview() = DebugContentPreview {
    HummusBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        LoadingTitle(text = "Loading")
    }
}