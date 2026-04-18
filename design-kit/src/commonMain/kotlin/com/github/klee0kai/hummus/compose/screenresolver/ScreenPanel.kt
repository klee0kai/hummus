package com.github.klee0kai.hummus.compose.screenresolver

import androidx.compose.runtime.Composable

class ScreenPanel(
    val list: @Composable () -> Unit,
    val details: (@Composable () -> Unit)? = null,
    val extras: (@Composable () -> Unit)? = null,
)