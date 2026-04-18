package com.github.klee0kai.hummus.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly


@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    )
}

@OptIn(DebugOnly::class)
@Composable
fun EmptyScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    )
}

