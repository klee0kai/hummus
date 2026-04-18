package com.github.klee0kai.hummus.compose.components.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.design.core.DesignComponent

@DesignComponent
@Composable
fun SimpleButton(
    text: String = "Click me",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = text)
    }
}

@DesignComponent
@Composable
fun StyledCard(
    title: String = "Card Title",
    subtitle: String = "Subtitle",
    isExpanded: Boolean = false,
) {
    Box(modifier = Modifier.padding(8.dp)) {
        Text(text = title)
        if (isExpanded) {
            Text(text = subtitle)
        }
    }
}
