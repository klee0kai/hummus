package com.github.klee0kai.hummus.storybook

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.klee0kai.hummus.storybook.AppContent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val path = window.location.pathname.dropWhile { it == '/' }
    ComposeViewport {
        AppContent()

//        App()
//        when (path) {
//            "privacy" -> App()
//            else -> App()
//        }
    }
}