package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.background
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.coral
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.grayColor
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.green
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.noColor
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.orange
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.pink
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.surfaceColor
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.turquoise
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.violet
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.whiteColor
import com.github.klee0kai.hummus.compose.theme.color.LightColorCollection.yellow

internal object LightColorCollection {
    /**
     * window background
     */
    val background = Color(0xFF7A84CC)

    /**
     * NavBoard headed
     * input fields, skeletons
     */
    val surfaceColor = Color(0xFF4D5280)

    val blackColor = Color.Black
    val whiteColor = Color.White
    val grayColor = Color(0xFFB7B7B7)

    val noColor = SurfaceScheme(grayColor, whiteColor)
    val violet = SurfaceScheme(Color(0xFF837AE8), whiteColor)
    val green = SurfaceScheme(Color(0xFF36C817), whiteColor)
    val yellow = SurfaceScheme(Color(0xFFF0EC1C), whiteColor)
    val turquoise = SurfaceScheme(Color(0xFF7AE8E8), whiteColor)
    val pink = SurfaceScheme(Color(0xFFE87AD6), whiteColor)
    val orange = SurfaceScheme(Color(0xFFDC8938), whiteColor)
    val coral = SurfaceScheme(Color(0xFFE87A7A), whiteColor)
}

fun lightCommonColorScheme() = HummusColorScheme(
    isDark = false,
    windowBackgroundColor = background,
    cardsBackground = surfaceColor,
    skeletonColor = surfaceColor,
    navigationBoard = NavigationBoardColors(
        headerBackgroundColor = surfaceColor,
        bodyBackgroundColor = Color(0xFF1C1D27),
    ),
    popupMenu = PopupMenuColors(
        surfaceColor = surfaceColor,
        contentColor = grayColor,
        shadowColor = Color.Black.copy(alpha = 0.2f)
    ),
    whiteTextButtonColors = ButtonColors(
        contentColor = whiteColor,
        containerColor = Color.Transparent,
        disabledContainerColor = whiteColor,
        disabledContentColor = Color.Transparent,
    ),
    grayTextButtonColors = ButtonColors(
        contentColor = grayColor,
        containerColor = Color.Transparent,
        disabledContainerColor = grayColor,
        disabledContentColor = Color.Transparent,
    ),
    surfaceSchemas = SurfaceSchemas(
        noColor = noColor,
        violet = violet,
        turquoise = turquoise,
        pink = pink,
        orange = orange,
        coral = coral,
    ),
    textColors = TextColors(
        bodyTextColor = whiteColor,
        hintTextColor = grayColor,
        primaryTextColor = turquoise.surfaceColor,
        secondaryTextColor = orange.surfaceColor,
        errorTextColor = coral.surfaceColor,
    ),
    greenColor = green.surfaceColor,
    yellowColor = yellow.surfaceColor,
    redColor = coral.surfaceColor,
    androidColorScheme = lightColorScheme(
//        primary = turquoise.surfaceColor,
//        onPrimary = whiteColor,
//        secondary = orange.surfaceColor,
//        onSecondary = whiteColor,
//        tertiary = orange.surfaceColor,
//        onTertiary = whiteColor,
//
//        primaryContainer = turquoise.surfaceColor,
//        onPrimaryContainer = whiteColor,
//        secondaryContainer = orange.surfaceColor,
//        onSecondaryContainer = whiteColor,
//        tertiaryContainer = orange.surfaceColor,
//        onTertiaryContainer = whiteColor,
//
//        background = background,
//        onBackground = whiteColor,
//        outline = whiteColor,
//
//        surface = surfaceColor,
//        onSurface = whiteColor,
//        surfaceVariant = surfaceColor,
//        onSurfaceVariant = whiteColor,
//        inverseSurface = whiteColor,
//        inverseOnSurface = blackColor,
    )
)
