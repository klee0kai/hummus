package com.github.klee0kai.hummus.compose.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.github.klee0kai.hummus.design.core.DesignComponent

@DesignComponent
@Composable
fun BackMenuIcon(
    isMenu: Boolean = false,
) {
    Icon(
        imageVector = if (isMenu) {
            Icons.Filled.Menu
        } else {
            Icons.AutoMirrored.Default.ArrowBack
        },
        contentDescription = null,
    )
}