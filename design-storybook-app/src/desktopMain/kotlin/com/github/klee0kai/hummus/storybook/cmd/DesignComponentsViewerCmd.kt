package com.github.klee0kai.hummus.storybook.cmd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.storybook.screens.ColorSchemeBrowserScreen
import com.github.klee0kai.hummus.storybook.screens.TypographyBrowserScreen
import com.github.klee0kai.storybook.screens.DesignComponentsBrowser
import picocli.CommandLine

enum class ViewerTheme {
    LIGHT, DARK
}

/**
 * Command to launch the Design Components Viewer desktop application
 */
@CommandLine.Command(
    name = "viewer",
    description = ["Launch interactive design components viewer"],
    mixinStandardHelpOptions = true
)
class DesignComponentsViewerCmd : Runnable {

    @CommandLine.Option(
        names = ["-t", "--theme"],
        description = ["Theme to use: LIGHT, DARK"],
        defaultValue = "LIGHT"
    )
    var theme: ViewerTheme = ViewerTheme.LIGHT

    @CommandLine.Option(
        names = ["-w", "--width"],
        description = ["Window width in pixels"],
        defaultValue = "1400"
    )
    var width: Int = 1400

    @CommandLine.Option(
        names = ["-h", "--height"],
        description = ["Window height in pixels"],
        defaultValue = "900"
    )
    var height: Int = 900

    override fun run() {
        launchDesktopApp(width, height, theme)
    }

    private fun launchDesktopApp(width: Int, height: Int, theme: ViewerTheme) {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Design Components Viewer",
                state = WindowState(width = width.dp, height = height.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppContent()
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
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

}
