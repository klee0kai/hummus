package com.github.klee0kai.hummus.compose.components.appbar

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.components.text.DotsFlashing
import com.github.klee0kai.hummus.compose.debug.DebugContentPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.utils.possitions.pxToDp

@Composable
fun LoadingTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    var baselinePadding by remember { mutableFloatStateOf(0f) }
    val dotSize = 2.2.dp
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = modifier,
            text = text,
            onTextLayout = { layout ->
                baselinePadding = layout.firstBaseline

            }
        )
        if (baselinePadding > 0f) {
            DotsFlashing(
                modifier = Modifier.padding(
                    start = 3.dp,
                    top = baselinePadding.pxToDp() - dotSize,
                ),
                dotsCount = 3,
                dotSize = dotSize,
            )
        }

    }

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview
fun StorageLoadingTitlePreview() = DebugContentPreview {
    HummusBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        LoadingTitle(text = "Loading")
    }
}