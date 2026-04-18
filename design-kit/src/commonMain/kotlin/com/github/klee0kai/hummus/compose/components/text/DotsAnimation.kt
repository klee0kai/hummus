package com.github.klee0kai.hummus.compose.components.text

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.LocalHummusTheme

@Composable
fun DotsFlashing(
    modifier: Modifier = Modifier,
    color: Color = LocalHummusTheme.current.colorScheme.textColors.bodyTextColor,
    dotSize: Dp = 24.dp,
    spaceSize: Dp = 2.dp,
    dotsCount: Int = 3,
    minAlpha: Float = 0.1f,
) {

    val infiniteTransition = rememberInfiniteTransition(label = "dots animation")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val delayUnit = 300

        repeat(dotsCount) { idx ->
            val alpha by infiniteTransition.animateFadeInOut(
                minAlpha = minAlpha,
                delayUnit = delayUnit,
                delay = delayUnit * idx,
            )

            Spacer(
                Modifier
                    .size(dotSize)
                    .alpha(alpha)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
            if (idx < dotsCount - 1) Spacer(Modifier.width(spaceSize))
        }
    }
}

@Composable
private fun InfiniteTransition.animateFadeInOut(
    minAlpha: Float = 0.1f,
    delayUnit: Int,
    delay: Int,
) = animateFloat(
    initialValue = minAlpha,
    targetValue = minAlpha,
    animationSpec = infiniteRepeatable(
        animation = keyframes {
            durationMillis = delayUnit * 4
            minAlpha at delay using LinearEasing
            1f at delay + delayUnit using LinearEasing
            minAlpha at delay + delayUnit * 2
        }
    ),
    label = "alpha with delay"
)

