package com.github.klee0kai.storybook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import com.github.klee0kai.hummus.compose.theme.color.HummusColorScheme
import com.github.klee0kai.hummus.compose.theme.color.NavigationBoardColors
import com.github.klee0kai.hummus.compose.theme.color.PopupMenuColors
import com.github.klee0kai.hummus.compose.theme.color.SurfaceScheme
import com.github.klee0kai.hummus.compose.theme.color.TextColors

@Composable
fun ColorSchemeEditor(
    scheme: HummusColorScheme,
    onSchemeChange: (HummusColorScheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            SectionHeader("Main Colors")
            SimpleColorRow(
                label = "Window BG",
                color = scheme.windowBackgroundColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(windowBackgroundColor = it))
                }
            )
            SimpleColorRow(
                label = "Cards BG",
                color = scheme.cardsBackground,
                onColorChange = {
                    onSchemeChange(scheme.copy(cardsBackground = it))
                }
            )
            SimpleColorRow(
                label = "Skeleton",
                color = scheme.skeletonColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(skeletonColor = it))
                }
            )
        }

        item {
            SectionHeader("Status Colors")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                SimpleColorRow(
                    label = "Green",
                    color = scheme.greenColor,
                    onColorChange = { onSchemeChange(scheme.copy(greenColor = it)) },
                    modifier = Modifier.weight(1f)
                )
                SimpleColorRow(
                    label = "Yellow",
                    color = scheme.yellowColor,
                    onColorChange = { onSchemeChange(scheme.copy(yellowColor = it)) },
                    modifier = Modifier.weight(1f)
                )
                SimpleColorRow(
                    label = "Red",
                    color = scheme.redColor,
                    onColorChange = { onSchemeChange(scheme.copy(redColor = it)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader("Text Colors")
            val textColors = scheme.textColors
            SimpleColorRow(
                label = "Body",
                color = textColors.bodyTextColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        textColors = textColors.copy(bodyTextColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Hint",
                color = textColors.hintTextColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        textColors = textColors.copy(hintTextColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Primary",
                color = textColors.primaryTextColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        textColors = textColors.copy(primaryTextColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Secondary",
                color = textColors.secondaryTextColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        textColors = textColors.copy(secondaryTextColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Error",
                color = textColors.errorTextColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        textColors = textColors.copy(errorTextColor = it)
                    ))
                }
            )
        }

        item {
            SectionHeader("Navigation Board")
            val navBoard = scheme.navigationBoard
            SimpleColorRow(
                label = "Header",
                color = navBoard.headerBackgroundColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        navigationBoard = navBoard.copy(headerBackgroundColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Body",
                color = navBoard.bodyBackgroundColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        navigationBoard = navBoard.copy(bodyBackgroundColor = it)
                    ))
                }
            )
        }

        item {
            SectionHeader("Popup Menu")
            val popup = scheme.popupMenu
            SimpleColorRow(
                label = "Surface",
                color = popup.surfaceColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        popupMenu = popup.copy(surfaceColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Content",
                color = popup.contentColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        popupMenu = popup.copy(contentColor = it)
                    ))
                }
            )
            SimpleColorRow(
                label = "Shadow",
                color = popup.shadowColor,
                onColorChange = {
                    onSchemeChange(scheme.copy(
                        popupMenu = popup.copy(shadowColor = it)
                    ))
                }
            )
        }

        item {
            SectionHeader("Surface Schemes")
            val surfaces = scheme.surfaceSchemas

            SurfaceSchemeRow(
                label = "Violet",
                scheme = surfaces.violet,
                onSchemeChange = {
                    onSchemeChange(scheme.copy(
                        surfaceSchemas = surfaces.copy(violet = it)
                    ))
                }
            )
            SurfaceSchemeRow(
                label = "Turquoise",
                scheme = surfaces.turquoise,
                onSchemeChange = {
                    onSchemeChange(scheme.copy(
                        surfaceSchemas = surfaces.copy(turquoise = it)
                    ))
                }
            )
            SurfaceSchemeRow(
                label = "Pink",
                scheme = surfaces.pink,
                onSchemeChange = {
                    onSchemeChange(scheme.copy(
                        surfaceSchemas = surfaces.copy(pink = it)
                    ))
                }
            )
            SurfaceSchemeRow(
                label = "Orange",
                scheme = surfaces.orange,
                onSchemeChange = {
                    onSchemeChange(scheme.copy(
                        surfaceSchemas = surfaces.copy(orange = it)
                    ))
                }
            )
            SurfaceSchemeRow(
                label = "Coral",
                scheme = surfaces.coral,
                onSchemeChange = {
                    onSchemeChange(scheme.copy(
                        surfaceSchemas = surfaces.copy(coral = it)
                    ))
                }
            )
        }
    }
}

@Composable
fun SimpleColorRow(
    label: String,
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ColorPickerDialog(color, onColorChange, onDismiss = { showDialog = false })
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, style = TextStyle(fontSize = 11.sp), modifier = Modifier.width(80.dp))
        ColorSwatch(color, onClick = { showDialog = true }, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SurfaceSchemeRow(
    label: String,
    scheme: SurfaceScheme,
    onSchemeChange: (SurfaceScheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSurfaceDialog by remember { mutableStateOf(false) }
    var showOnSurfaceDialog by remember { mutableStateOf(false) }

    if (showSurfaceDialog) {
        ColorPickerDialog(scheme.surfaceColor, { onSchemeChange(scheme.copy(surfaceColor = it)) }, onDismiss = { showSurfaceDialog = false })
    }

    if (showOnSurfaceDialog) {
        ColorPickerDialog(scheme.onSurfaceColor, { onSchemeChange(scheme.copy(onSurfaceColor = it)) }, onDismiss = { showOnSurfaceDialog = false })
    }

    Column(modifier.padding(4.dp)) {
        Text(label, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fg", style = TextStyle(fontSize = 10.sp), modifier = Modifier.width(30.dp))
            ColorSwatch(scheme.surfaceColor, onClick = { showSurfaceDialog = true }, modifier = Modifier.weight(1f))
            Text("Bg", style = TextStyle(fontSize = 10.sp), modifier = Modifier.width(30.dp))
            ColorSwatch(scheme.onSurfaceColor, onClick = { showOnSurfaceDialog = true }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    )
}
