package com.github.klee0kai.hummus.storybook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.storybook.storybook.screens.ColorSchemeBrowserScreen
import com.github.klee0kai.hummus.storybook.storybook.screens.DesignComponentsBrowser
import com.github.klee0kai.hummus.storybook.storybook.screens.TypographyBrowserScreen

@Composable
fun AppContent() {
    HummusTheme {
        var selectedTab by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    text = { Text("Components") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                Tab(
                    text = { Text("Color Scheme") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                Tab(
                    text = { Text("Typography") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }

            when (selectedTab) {
                0 -> DesignComponentsBrowser(modifier = Modifier.fillMaxSize())
                1 -> ColorSchemeBrowserScreen(modifier = Modifier.fillMaxSize())
                2 -> TypographyBrowserScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}