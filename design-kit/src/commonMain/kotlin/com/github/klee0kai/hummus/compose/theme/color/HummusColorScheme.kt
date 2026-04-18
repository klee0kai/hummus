package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Stable
data class HummusColorScheme(
    val isDark: Boolean,
    val windowBackgroundColor: Color,
    /**
     * Bottom sheet/card background
     */
    val cardsBackground: Color,
    val skeletonColor: Color,
    val navigationBoard: NavigationBoardColors,
    val popupMenu: PopupMenuColors,
    val whiteTextButtonColors: ButtonColors,
    val grayTextButtonColors: ButtonColors,
    val textColors: TextColors,

    val greenColor: Color,
    val yellowColor: Color,
    val redColor: Color,
    val surfaceSchemas: SurfaceSchemas,

    val androidColorScheme: ColorScheme,
)

@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: HummusColorScheme,
): State<HummusColorScheme> {
    val windowBackgroundColor = animateColorAsState(targetValue = colors.windowBackgroundColor)
    val cardsBackground = animateColorAsState(targetValue = colors.cardsBackground)
    val skeletonColor = animateColorAsState(colors.skeletonColor)
    val navigationBoard = animateColorAsState(colors.navigationBoard)
    val popupMenu = animateColorAsState(colors.popupMenu)
    val whiteTextButtonColors =
        animateColorAsState(colors.whiteTextButtonColors)
    val grayTextButtonColors =
        animateColorAsState(colors.grayTextButtonColors)
    val textColors = animateColorAsState(colors.textColors)
    val greenColor = animateColorAsState(colors.greenColor)
    val yellowColor = animateColorAsState(colors.yellowColor)
    val redColor = animateColorAsState(colors.redColor)
    val surfaceSchemas = animateColorAsState(colors.surfaceSchemas)
    val androidColorScheme =
        animateColorAsState(colors.androidColorScheme)

    return rememberDerivedStateOf {
        HummusColorScheme(
            isDark = colors.isDark,
            windowBackgroundColor = windowBackgroundColor.value,
            cardsBackground = cardsBackground.value,
            skeletonColor = skeletonColor.value,
            navigationBoard = navigationBoard.value,
            popupMenu = popupMenu.value,
            whiteTextButtonColors = whiteTextButtonColors.value,
            grayTextButtonColors = grayTextButtonColors.value,
            textColors = textColors.value,
            greenColor = greenColor.value,
            yellowColor = yellowColor.value,
            redColor = redColor.value,
            surfaceSchemas = surfaceSchemas.value,
            androidColorScheme = colors.androidColorScheme,
        )
    }
}

