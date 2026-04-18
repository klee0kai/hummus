package com.github.klee0kai.storybook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.theme.HummusTheme

@Composable
fun TypographyPreview(
    theme: HummusTheme,
    modifier: Modifier = Modifier,
) {
    HummusTheme(theme = theme) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(theme.colorScheme.windowBackgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PreviewTypographySection(
                    title = "Body Text",
                    style = theme.typeScheme.body,
                    description = "Main text in list items"
                )
            }

            item {
                PreviewTypographySection(
                    title = "Body Small",
                    style = theme.typeScheme.bodySmall,
                    description = "Additional text & hints"
                )
            }

            item {
                PreviewTypographySection(
                    title = "Screen Header",
                    style = theme.typeScheme.screenHeader,
                    description = "AppBar, tabs, dialogs"
                )
            }

            item {
                PreviewTypographySection(
                    title = "Header",
                    style = theme.typeScheme.header,
                    description = "Group headers, settings"
                )
            }

            item {
                PreviewTypographySection(
                    title = "Button Text",
                    style = theme.typeScheme.buttonText,
                    description = "Button text style"
                )
            }

            item {
                ComponentUsageExamples(theme)
            }
        }
    }
}

@Composable
private fun PreviewTypographySection(
    title: String,
    style: TextStyle,
    description: String = "",
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("The quick brown fox jumps over the lazy dog.", style = style)
                Text("Pack my box with five dozen liquor jugs.", style = style)
                Text(description, style = style.copy(fontSize = style.fontSize * 0.8))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Size: ${style.fontSize.value.toInt()}sp", style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp))
                Text("Weight: ${style.fontWeight?.weight ?: "normal"}", style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp))
                Text("Height: ${style.lineHeight.value.toInt()}sp", style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp))
            }
        }

        Divider()
    }
}

@Composable
private fun ComponentUsageExamples(theme: HummusTheme) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Component Examples",
            style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("List Item Title", style = theme.typeScheme.header)
                Text("List item description and additional info", style = theme.typeScheme.bodySmall)
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Button", style = theme.typeScheme.buttonText)
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Outlined", style = theme.typeScheme.buttonText)
            }
        }

        TextField(
            value = "TextField with body small hint",
            onValueChange = {},
            label = { Text("Input label", style = theme.typeScheme.header) },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("AppBar-like header", style = theme.typeScheme.screenHeader)
            Text("Body text below header", style = theme.typeScheme.body)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Group Title", style = theme.typeScheme.header)
            Text("Item description", style = theme.typeScheme.bodySmall)
            Divider()
            Text("Another item", style = theme.typeScheme.body)
            Text("With additional info", style = theme.typeScheme.bodySmall)
        }
    }
}
