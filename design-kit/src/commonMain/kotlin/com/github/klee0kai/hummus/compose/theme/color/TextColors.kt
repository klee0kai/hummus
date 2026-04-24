package com.github.klee0kai.hummus.compose.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.hummus.compose.utils.views.rememberDerivedStateOf

@Stable
data class TextColors(
    val bodyTextColor: Color,
    val hintTextColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val errorTextColor: Color,
)


@Composable
@NonRestartableComposable
fun animateColorAsState(
    colors: TextColors,
): State<TextColors> {
    val bodyTextColor = animateColorAsState(
        targetValue = colors.bodyTextColor,
        label = ""
    )
    val hintTextColor = animateColorAsState(
        targetValue = colors.hintTextColor,
        label = ""
    )
    val primaryTextColor = animateColorAsState(
        targetValue = colors.primaryTextColor,
        label = ""
    )
    val secondaryTextColor = animateColorAsState(
        targetValue = colors.secondaryTextColor,
        label = ""
    )
    val errorTextColor = animateColorAsState(
        targetValue = colors.errorTextColor,
        label = ""
    )


    return rememberDerivedStateOf {
        TextColors(
            bodyTextColor = bodyTextColor.value,
            hintTextColor = hintTextColor.value,
            primaryTextColor = primaryTextColor.value,
            secondaryTextColor = secondaryTextColor.value,
            errorTextColor = errorTextColor.value,
        )
    }
}