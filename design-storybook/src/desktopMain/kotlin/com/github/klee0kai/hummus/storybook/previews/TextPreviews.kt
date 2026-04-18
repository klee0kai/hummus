package com.github.klee0kai.hummus.storybook.previews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.components.text.DotsFlashing
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.debug.DebugContentPreview
import com.github.klee0kai.hummus.compose.debug.annotations.DebugOnly
import com.github.klee0kai.hummus.compose.utils.views.transparentColors


@OptIn(DebugOnly::class)
@Composable
@Preview
fun AppTextFieldPreview() = DebugContentPreview {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        HummusTextField(
            modifier = Modifier,
            value = TextFieldValue("User some input text"),
            label = {
                Text(text = "label")
            }
        )
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun AppTextFieldSkeletonPreview() = DebugContentPreview {
    Box(
        modifier = Modifier.padding(10.dp),
    ) {
        HummusTextField(
            modifier = Modifier,
            isSkeleton = true,
            value = "User some input text",
            label = {
                Text(text = "label")
            }
        )
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun AppTextEmptyFieldPreview() = DebugContentPreview {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        HummusTextField(
            modifier = Modifier,
            value = "",
            label = {
                Text(text = "label")
            }
        )
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun AppTransparentTextFieldPreview() = DebugContentPreview {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        HummusTextField(
            modifier = Modifier,
            value = TextFieldValue("User some input text"),
            label = {
                Text(text = "label")
            },
            colors = TextFieldDefaults.transparentColors(),
        )
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun AppTransparentTextEmptyFieldPreview() = DebugContentPreview {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        HummusTextField(
            modifier = Modifier,
            value = "",
            label = {
                Text(text = "label")
            },
            colors = TextFieldDefaults.transparentColors(),
        )
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun DotsFlashingPreview() = DebugContentPreview {
    DotsFlashing(
        dotsCount = 4,
    )
}
