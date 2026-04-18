package com.github.klee0kai.hummus.compose.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val icon: ImageVector,
    val text: String,
    val destination: Destination,
)
