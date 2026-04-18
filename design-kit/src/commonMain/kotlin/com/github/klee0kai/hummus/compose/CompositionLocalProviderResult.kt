package com.github.klee0kai.hummus.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.currentComposer

@Composable
@OptIn(InternalComposeApi::class)
fun <Res> CompositionLocalProviderRes(
    value: ProvidedValue<*>,
    content: @Composable () -> Res,
): Res {
    currentComposer.startProvider(value)
    val res = content()
    currentComposer.endProvider()
    return res
}