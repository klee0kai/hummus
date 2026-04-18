package com.github.klee0kai.hummus.storybook.storybook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight as ComposeW
import kotlin.math.roundToInt

@Composable
fun TypeSchemeEditor(
    body: TextStyle,
    bodySmall: TextStyle,
    screenHeader: TextStyle,
    header: TextStyle,
    buttonText: TextStyle,
    onBodyChange: (TextStyle) -> Unit,
    onBodySmallChange: (TextStyle) -> Unit,
    onScreenHeaderChange: (TextStyle) -> Unit,
    onHeaderChange: (TextStyle) -> Unit,
    onButtonTextChange: (TextStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SectionHeader("Text Styles")

            TextStyleEditor(
                label = "Body",
                style = body,
                onStyleChange = onBodyChange,
                description = "Main text in list items"
            )

            TextStyleEditor(
                label = "Body Small",
                style = bodySmall,
                onStyleChange = onBodySmallChange,
                description = "Additional text & hints"
            )

            TextStyleEditor(
                label = "Screen Header",
                style = screenHeader,
                onStyleChange = onScreenHeaderChange,
                description = "AppBar, tabs, dialogs"
            )

            TextStyleEditor(
                label = "Header",
                style = header,
                onStyleChange = onHeaderChange,
                description = "Group headers, settings"
            )

            TextStyleEditor(
                label = "Button Text",
                style = buttonText,
                onStyleChange = onButtonTextChange,
                description = "Button text style"
            )
        }
    }
}

@Composable
fun TextStyleEditor(
    label: String,
    style: TextStyle,
    onStyleChange: (TextStyle) -> Unit,
    modifier: Modifier = Modifier,
    description: String = "",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = TextStyle(fontWeight = FontWeight.Bold))
                if (description.isNotEmpty()) {
                    Text(description, style = TextStyle(fontSize = 10.sp))
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Size", modifier = Modifier.width(50.dp), style = TextStyle(fontSize = 11.sp))
            Slider(
                value = style.fontSize.value,
                onValueChange = { newSize ->
                    onStyleChange(style.copy(fontSize = newSize.sp))
                },
                valueRange = 8f..32f,
                modifier = Modifier.weight(1f)
            )
            Text("${style.fontSize.value.roundToInt()}sp", modifier = Modifier.width(40.dp), style = TextStyle(fontSize = 10.sp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Weight", modifier = Modifier.width(50.dp), style = TextStyle(fontSize = 11.sp))
            FontWeightSelector(
                fontWeight = style.fontWeight ?: FontWeight.Normal,
                onWeightChange = { newWeight ->
                    onStyleChange(style.copy(fontWeight = newWeight))
                },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Height", modifier = Modifier.width(50.dp), style = TextStyle(fontSize = 11.sp))
            Slider(
                value = style.lineHeight.value,
                onValueChange = { newHeight ->
                    onStyleChange(style.copy(lineHeight = newHeight.sp))
                },
                valueRange = 8f..48f,
                modifier = Modifier.weight(1f)
            )
            Text("${style.lineHeight.value.roundToInt()}sp", modifier = Modifier.width(40.dp), style = TextStyle(fontSize = 10.sp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Preview: The quick brown fox",
                style = style
            )
        }
    }
}

@Composable
fun FontWeightSelector(
    fontWeight: FontWeight,
    onWeightChange: (FontWeight) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val weights = listOf(
        ComposeW.Thin,
        ComposeW.ExtraLight,
        ComposeW.Light,
        ComposeW.Normal,
        ComposeW.Medium,
        ComposeW.SemiBold,
        ComposeW.Bold,
        ComposeW.ExtraBold,
        ComposeW.Black,
    )

    Box(modifier = modifier) {
        Text(
            fontWeight.weight.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp),
            style = TextStyle(fontSize = 11.sp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            weights.forEach { weight ->
                DropdownMenuItem(
                    text = { Text(weight.weight.toString(), style = TextStyle(fontSize = 10.sp)) },
                    onClick = {
                        onWeightChange(weight)
                        expanded = false
                    }
                )
            }
        }
    }
}
