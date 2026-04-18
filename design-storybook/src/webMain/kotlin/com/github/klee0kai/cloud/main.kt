package com.github.klee0kai.cloud

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.klee0kai.cloud.devkit.components.grapth.MyGraphViewer

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
//    val path = window.location.pathname.dropWhile { it == '/' }
    ComposeViewport {
        MyGraphViewer()

//        App()
//        when (path) {
//            "privacy" -> App()
//            else -> App()
//        }
    }
}