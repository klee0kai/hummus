package com.github.klee0kai.hummus.storybook.previews

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarConst
import com.github.klee0kai.hummus.compose.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.hummus.compose.components.overlay.PopupMenu
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.debug.DebugScreenPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.utils.possitions.onGlobalPositionState
import com.github.klee0kai.hummus.compose.utils.possitions.rememberViewPosition
import com.github.klee0kai.hummus.compose.utils.views.appContent
import com.github.klee0kai.hummus.compose.utils.views.horizontal
import com.github.klee0kai.hummus.compose.utils.views.linkToParent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuSimplePreview() = DebugScreenPreview {
    val theme = LocalHummusTheme.current
    val textFieldPos = rememberViewPosition()
    var popupVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp)
                .onGlobalPositionState(textFieldPos)
                .background(Color.Gray)
                .clickable { popupVisible = true },
        )
        Spacer(modifier = Modifier.weight(1f))
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = textFieldPos,
        horizontalBias = 0.8f,
        onDismissRequest = { popupVisible = false },
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(0.6f)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuSmallButtonPreview() = DebugScreenPreview() {
    val theme = LocalHummusTheme.current
    val textFieldPos = rememberViewPosition()
    var popupVisible by remember { mutableStateOf(true) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (buttonField) = createRefs()
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 50.dp)
                .onGlobalPositionState(textFieldPos)
                .background(Color.Gray)
                .clickable { popupVisible = !popupVisible }
                .constrainAs(buttonField) {
                    linkToParent(
                        verticalBias = 0.5f,
                        horizontalBias = 1f,
                    )
                },
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = textFieldPos,
        horizontalBias = 0.2f,
        ignoreAnchorSize = true,
        onDismissRequest = { popupVisible = false },
    ) {
        SimpleSelectPopupMenu(
            variants = listOf(
                "item 1",
                "item 2",
                "item 3",
            )
        )
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuInsetsPreview() = DebugScreenPreview() {
    val theme = LocalHummusTheme.current
    var popupVisible by remember { mutableStateOf(true) }
    val storagePathPosition = rememberViewPosition()
    val appContentPaddings = WindowInsets.appContent.asPaddingValues()
    var additionalPadding by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        while (isActive) {
            repeat(60) {
                delay(100)
                additionalPadding += 10.dp
            }
            repeat(60) {
                delay(100)
                additionalPadding -= 10.dp
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
    ) {
        val (
            pathTextField,
        ) = createRefs()

        HummusTextField(
            modifier = Modifier
                .onFocusChanged { }
                .onGlobalPositionState(storagePathPosition)
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + HummusBarConst.appBarSize + additionalPadding,
                        startMargin = appContentPaddings.horizontal(),
                        endMargin = appContentPaddings.horizontal()
                    )
                },
            value = "text",
            label = { "text" }
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = storagePathPosition,
        horizontalBias = 0.8f,
        onDismissRequest = { popupVisible = false },
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(0.6f)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuMaxSizePreview() = DebugScreenPreview() {
    val theme = LocalHummusTheme.current
    var popupVisible by remember { mutableStateOf(true) }
    val storagePathPosition = rememberViewPosition()
    val appContentPaddings = WindowInsets.appContent.asPaddingValues()
    var additionalPadding by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        while (isActive) {
            repeat(60) {
                delay(100)
                additionalPadding += 10.dp
            }
            repeat(60) {
                delay(100)
                additionalPadding -= 10.dp
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
    ) {
        val (
            pathTextField,
        ) = createRefs()

        HummusTextField(
            modifier = Modifier
                .onFocusChanged { }
                .onGlobalPositionState(storagePathPosition)
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + HummusBarConst.appBarSize + additionalPadding,
                        startMargin = appContentPaddings.horizontal(),
                        endMargin = appContentPaddings.horizontal()
                    )
                },
            value = "text",
            label = { "text" }
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = storagePathPosition,
        horizontalBias = 0.8f,
        onDismissRequest = { popupVisible = false },
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.6f)
                .padding(vertical = 10.dp)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}
