package com.github.klee0kai.hummus.storybook.cmd

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.storybook.screens.DesignComponentsBrowser
import picocli.CommandLine

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
    var theme: String = "LIGHT"

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
        launchDesktopApp(
            width = width,
            height = height,
            useDarkTheme = theme.uppercase() == "DARK"
        )
    }

    private fun launchDesktopApp(width: Int, height: Int, useDarkTheme: Boolean) {
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
            DesignComponentsBrowser()
        }
    }

}
