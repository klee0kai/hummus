package com.github.klee0kai.hummus.compose.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarConst
import com.github.klee0kai.hummus.compose.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.debug.DebugScreenPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.utils.possitions.*
import com.github.klee0kai.hummus.compose.utils.views.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration

private enum class PopupGravity {
    TOP,
    BOTTOM,
}

@Composable
fun PopupMenu(
    visible: Boolean,
    positionAnchor: State<ViewPositionPx?>,
    horizontalBias: Float = 0f,
    onDismissRequest: (() -> Unit)? = null,
    shadowColor: Color = LocalHummusTheme.current.colorScheme.popupMenu.shadowColor,
    ignoreAnchorSize: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val visibleAlpha by animateAlphaAsState(visible)
    val theme = LocalHummusTheme.current

    LocalHummusRouter.current.BackHandler(isEnabled = visible) {
        onDismissRequest?.invoke()
    }

    if (visibleAlpha > 0f) {
        val overlayProvider = LocalOverlayProvider.current
        val appContentPaddings = WindowInsets.appContent.asPaddingValues()
        val imePadding = WindowInsets.ime.asPaddingValues()
        val overlayKey = listOf(content.hashCode())
        DisposableEffect(key1 = Unit) {
            onDispose { overlayProvider.clean(overlayKey) }
        }

        val density = LocalDensity.current
        val screenWidthDp = with(density) {
            WindowInsets.appContent.getLeft(this, LayoutDirection.Ltr).toDp() +
                    WindowInsets.appContent.getRight(this, LayoutDirection.Ltr).toDp()
        }
        val screenHeightDp = with(density) {
            WindowInsets.appContent.getLeft(this, LayoutDirection.Ltr).toDp() +
                    WindowInsets.appContent.getRight(this, LayoutDirection.Ltr).toDp()
        }

        LocalOverlayProvider.current.Overlay(overlayKey) {
            val density = LocalDensity.current
            val bias = if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                horizontalBias
            } else {
                1f - horizontalBias
            }
            val anchorDp by rememberDerivedStateOf(overlayKey) { positionAnchor.value?.toDp(density) }
            val contentPosPx = remember(overlayKey) { mutableStateOf<ViewPositionPx?>(null) }
            val contentPosDp by rememberDerivedStateOf(overlayKey) {
                contentPosPx.value?.toDp(density) ?: ViewPositionDp()
            }
            val gravity by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf null

                    when {
                        contentPosPx.value == null || contentPosPx.value?.size?.height == 0 -> null

                        screenHeightDp - appContentPaddings.calculateBottomPadding() - imePadding.calculateBottomPadding()
                                < anchor.globalPos.y + anchor.size.height + contentPosDp.size.height
                                && anchor.globalPos.y > screenHeightDp / 3f -> PopupGravity.TOP

                        else -> PopupGravity.BOTTOM
                    }
                }
            }
            val offset by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf DpOffset(0.dp, 0.dp)

                    var offset = when (gravity) {
                        PopupGravity.TOP -> {
                            DpOffset(
                                x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                                y = anchor.globalPos.y - contentPosDp.size.height,
                            )
                        }

                        PopupGravity.BOTTOM -> {
                            DpOffset(
                                x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                                y = anchor.globalPos.y + anchor.size.height,
                            )
                        }

                        else -> return@rememberDerivedStateOf DpOffset(0.dp, 0.dp)
                    }
                    if (offset.x + contentPosDp.size.width > screenWidthDp - appContentPaddings.horizontal()) {
                        offset = offset.copy(
                            x = screenWidthDp - appContentPaddings.horizontal() - contentPosDp.size.width
                        )
                    }

                    offset
                }
            }
            val contentMaxHeight by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf Dp.Unspecified
                    when (gravity) {
                        PopupGravity.TOP -> {
                            anchor.globalPos.y - appContentPaddings.calculateTopPadding()
                        }

                        PopupGravity.BOTTOM -> {
                            screenHeightDp - appContentPaddings.calculateBottomPadding() - anchor.globalPos.y - anchor.size.height
                        }

                        null -> Dp.Unspecified
                    }
                }
            }

            val leftPopupShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = contentPosDp.globalPos.y),
                        size = DpSize(
                            width = contentPosDp.globalPos.x,
                            height = contentPosDp.size.height
                        )
                    )
                }
            }

            val rightPopupShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    ViewPositionDp(
                        globalPos = DpOffset(
                            x = contentPosDp.globalPos.x + contentPosDp.size.width,
                            y = contentPosDp.globalPos.y
                        ),
                        size = DpSize(
                            width = screenWidthDp - (contentPosDp.globalPos.x + contentPosDp.size.width),
                            height = contentPosDp.size.height
                        )
                    )
                }
            }

            val leftAnchorShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = anchor.globalPos.y),
                        size = DpSize(width = anchor.globalPos.x, height = anchor.size.height)
                    )
                }
            }

            val rightAnchorShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(
                            x = anchor.globalPos.x + anchor.size.width,
                            y = anchor.globalPos.y
                        ),
                        size = DpSize(
                            width = screenWidthDp - (anchor.globalPos.x + anchor.size.width),
                            height = anchor.size.height
                        )
                    )
                }
            }

            val bottomShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    val y = max(
                        anchor.globalPos.y + anchor.size.height,
                        contentPosDp.globalPos.y + contentPosDp.size.height
                    )
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = y),
                        size = DpSize(width = screenWidthDp, height = screenHeightDp - y)
                    )
                }
            }

            val topShadow by rememberDerivedStateOf(overlayKey) {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = 0.dp),
                        size = DpSize(
                            width = screenWidthDp,
                            height = min(anchor.globalPos.y, contentPosDp.globalPos.y)
                        )
                    )
                }
            }

            val positionAvailableAlpha by rememberAlphaAnimate(warmUpTime = Duration.ZERO) { contentPosPx.value != null && visible }
            val fullAnimatedAlpha by rememberDerivedStateOf(overlayKey) { positionAvailableAlpha * visibleAlpha }
            Box(
                modifier = Modifier
                    .thenIf(!ignoreAnchorSize) {
                        sizeIn(
                            maxWidth = anchorDp?.size?.width ?: 0.dp,
                            maxHeight = contentMaxHeight ?: 0.dp,
                        )
                    }
                    .absoluteOffset(offset.x, offset.y)
                    .onGlobalPositionState(contentPosPx)
                    .background(shadowColor.copy(alpha = shadowColor.alpha * fullAnimatedAlpha))
                    .alpha(fullAnimatedAlpha),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides theme.typeScheme.header,
                ) {
                    content()
                }
            }

            Box(
                modifier = Modifier
                    .placeTo(leftPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(rightPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )
            Box(
                modifier = Modifier
                    .placeTo(leftAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(rightAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(bottomShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(topShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )
        }
    }
}


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