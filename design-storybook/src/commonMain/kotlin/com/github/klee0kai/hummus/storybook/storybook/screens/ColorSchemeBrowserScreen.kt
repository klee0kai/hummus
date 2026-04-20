package com.github.klee0kai.hummus.storybook.storybook.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes
import com.github.klee0kai.hummus.storybook.storybook.components.ColorSchemeEditor
import com.github.klee0kai.hummus.storybook.storybook.components.MultiColorComponentSelector
import com.github.klee0kai.hummus.storybook.storybook.components.MultiColorPickerDialog
import com.github.klee0kai.hummus.storybook.storybook.components.ThemePreview

@Composable
fun ColorSchemeBrowserScreen(modifier: Modifier = Modifier) {
    var baseThemeName by remember { mutableStateOf("Dark") }
    var baseTheme by remember { mutableStateOf(HummusDefaultThemes.darkTheme) }
    var colorScheme by remember { mutableStateOf(baseTheme.colorScheme) }
    var showMultiColorPicker by remember { mutableStateOf(false) }

    val previewTheme = baseTheme.copy(colorScheme = colorScheme)

    if (showMultiColorPicker) {
        MultiColorPickerDialog(
            colors = listOf(
                "Primary" to colorScheme.textColors.primaryTextColor,
                "Body" to colorScheme.textColors.bodyTextColor,
                "Hint" to colorScheme.textColors.hintTextColor,
                "Background" to colorScheme.windowBackgroundColor,
                "Card BG" to colorScheme.cardsBackground,
                "Green" to colorScheme.greenColor,
                "Yellow" to colorScheme.yellowColor,
                "Red" to colorScheme.redColor
            ),
            onColorsChange = { updatedColors ->
                colorScheme = colorScheme.copy(
                    textColors = colorScheme.textColors.copy(
                        primaryTextColor = updatedColors[0].second,
                        bodyTextColor = updatedColors[1].second,
                        hintTextColor = updatedColors[2].second
                    ),
                    windowBackgroundColor = updatedColors[3].second,
                    cardsBackground = updatedColors[4].second,
                    greenColor = updatedColors[5].second,
                    yellowColor = updatedColors[6].second,
                    redColor = updatedColors[7].second
                )
            },
            onDismiss = { showMultiColorPicker = false }
        )
    }

    Row(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            HummusTheme {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Base Theme:", style = TextStyle(fontSize = 12.sp))

                    RadioButton(
                        selected = baseThemeName == "Dark",
                        onClick = {
                            baseThemeName = "Dark"
                            baseTheme = HummusDefaultThemes.darkTheme
                            colorScheme = baseTheme.colorScheme
                        }
                    )
                    Text("Dark", style = TextStyle(fontSize = 11.sp))

                    RadioButton(
                        selected = baseThemeName == "Light",
                        onClick = {
                            baseThemeName = "Light"
                            baseTheme = HummusDefaultThemes.lightTheme
                            colorScheme = baseTheme.colorScheme
                        }
                    )
                    Text("Light", style = TextStyle(fontSize = 11.sp))
                }

                Button(
                    onClick = { showMultiColorPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Multi Color Picker", style = TextStyle(fontSize = 11.sp))
                }

                MultiColorComponentSelector(
                    label = "Status Colors",
                    colors = listOf(
                        "Green" to colorScheme.greenColor,
                        "Yellow" to colorScheme.yellowColor,
                        "Red" to colorScheme.redColor
                    ),
                    onColorsChange = { updatedColors ->
                        val updated = colorScheme.copy(
                            greenColor = updatedColors[0].second,
                            yellowColor = updatedColors[1].second,
                            redColor = updatedColors[2].second
                        )
                        colorScheme = updated
                    }
                )

                ColorSchemeEditor(
                    scheme = colorScheme,
                    onSchemeChange = { colorScheme = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ThemePreview(
            theme = previewTheme,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
        )
    }
}
