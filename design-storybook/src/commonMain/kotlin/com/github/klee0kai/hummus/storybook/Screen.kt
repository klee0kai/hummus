package com.github.klee0kai.hummus.storybook

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.components.overlay.OverlayContainer
import com.github.klee0kai.hummus.compose.utils.views.currentRef

@Composable
fun Screen(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = OverlayContainer {
    val router by LocalHummusRouter.currentRef
    val config by LocalHummusRouter.currentRef
//    val vm = remember { ComposeDI.appBarViewModel() }

    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

//    if (config?.isSetTitleAvailable == true) {
//        DisposableEffect(title) {
//            vm.setTitle(title)
//            onDispose {
//                vm.setTitle("")
//            }
//        }
//    }

    router?.BackHandler(isEnabled = isImeVisible) {
        router?.hideKeyboard()
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures { router?.hideKeyboard() } }
    ) {
        content()
    }
}