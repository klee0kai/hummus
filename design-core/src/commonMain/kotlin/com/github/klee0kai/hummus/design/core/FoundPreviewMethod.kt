package com.github.klee0kai.hummus.design.core

import androidx.compose.runtime.Composable

class FoundPreviewMethod(
    val pkg: String,
    val methodName: String,
    val annotations: List<Any>,
    val param: Any? = null,
    val paramIdx: Int = 0,
    val content: @Composable () -> Unit,
)