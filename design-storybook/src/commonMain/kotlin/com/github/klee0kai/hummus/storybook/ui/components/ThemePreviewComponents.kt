package com.github.klee0kai.hummus.storybook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.theme.HummusTheme
import com.github.klee0kai.hummus.compose.theme.color.SurfaceScheme

@Composable
fun ThemePreview(
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
                PreviewSection("Typography", theme)
            }
            item {
                PreviewSection("Colors & Buttons", theme)
            }
            item {
                PreviewSection("Cards & Surfaces", theme)
            }
            item {
                PreviewSection("Surface Schemes", theme)
            }
            item {
                PreviewSection("Material3 Components", theme)
            }
        }
    }
}

@Composable
private fun PreviewSection(
    title: String,
    theme: HummusTheme,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            color = theme.colorScheme.textColors.primaryTextColor
        )

        when (title) {
            "Typography" -> {
                Text(
                    "Body Text",
                    color = theme.colorScheme.textColors.bodyTextColor,
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    "Hint Text (Secondary)",
                    color = theme.colorScheme.textColors.hintTextColor,
                    style = TextStyle(fontSize = 11.sp)
                )
                Text(
                    "Primary Text",
                    color = theme.colorScheme.textColors.primaryTextColor,
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    "Error Text",
                    color = theme.colorScheme.textColors.errorTextColor,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            "Colors & Buttons" -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorIndicator("Green", theme.colorScheme.greenColor, Modifier.weight(1f))
                    ColorIndicator("Yellow", theme.colorScheme.yellowColor, Modifier.weight(1f))
                    ColorIndicator("Red", theme.colorScheme.redColor, Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f)) {
                        Text("Button")
                    }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                        Text("Outlined")
                    }
                }
            }

            "Cards & Surfaces" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.colorScheme.cardsBackground
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Card with cardsBackground color")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(theme.colorScheme.skeletonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Skeleton/Input background", fontSize = 11.sp)
                }
            }

            "Surface Schemes" -> {
                val surfaces = theme.colorScheme.surfaceSchemas
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    SurfaceSchemePreview("V", surfaces.violet, Modifier.weight(1f))
                    SurfaceSchemePreview("T", surfaces.turquoise, Modifier.weight(1f))
                    SurfaceSchemePreview("P", surfaces.pink, Modifier.weight(1f))
                    SurfaceSchemePreview("O", surfaces.orange, Modifier.weight(1f))
                    SurfaceSchemePreview("C", surfaces.coral, Modifier.weight(1f))
                }
            }

            "Material3 Components" -> {
                TextField(
                    value = "Sample text field",
                    onValueChange = {},
                    label = { Text("Input Label") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {}) {
                        Text("M3 Button")
                    }
                    OutlinedButton(onClick = {}) {
                        Text("M3 Outlined")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorIndicator(label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Text(label, style = TextStyle(fontSize = 10.sp))
    }
}

@Composable
private fun SurfaceSchemePreview(label: String, scheme: SurfaceScheme, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(scheme.surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(label, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = scheme.onSurfaceColor)
        }
    }
}
