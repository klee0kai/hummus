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
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarConst
import com.github.klee0kai.hummus.compose.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.utils.possitions.*
import com.github.klee0kai.hummus.compose.utils.views.*
import com.github.klee0kai.hummus.design.core.DesignComponent
import kotlin.time.Duration

private enum class PopupGravity {
    TOP,
    BOTTOM,
}

@DesignComponent
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


