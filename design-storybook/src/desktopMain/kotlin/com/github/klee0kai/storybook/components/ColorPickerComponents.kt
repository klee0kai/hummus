package com.github.klee0kai.storybook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ColorSwatch(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Column(modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color)
                .border(1.dp, Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                color.toHex(),
                style = TextStyle(fontSize = 6.sp, color = Color.White),
                modifier = Modifier.padding(2.dp)
            )
        }
        if (label != null) {
            Text(label, style = TextStyle(fontSize = 10.sp))
        }
    }
}

@Composable
fun ColorPickerPanel(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hsv = color.toHsv()
    var hue by remember { mutableStateOf(hsv.first) }
    var saturation by remember { mutableStateOf(hsv.second) }
    var value by remember { mutableStateOf(hsv.third) }
    var alpha by remember { mutableStateOf(color.alpha) }
    var hexInput by remember { mutableStateOf(color.toHex()) }

    fun updateColor() {
        val newColor = hsvToColor(hue, saturation, value, alpha)
        onColorChange(newColor)
        hexInput = newColor.toHex()
    }

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Hue", modifier = Modifier.width(40.dp))
            Slider(
                value = hue,
                onValueChange = { h ->
                    hue = h
                    updateColor()
                },
                valueRange = 0f..360f,
                modifier = Modifier.weight(1f)
            )
            Text("${hue.roundToInt()}°", style = TextStyle(fontSize = 11.sp), modifier = Modifier.width(40.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Sat", modifier = Modifier.width(40.dp))
            Slider(
                value = saturation,
                onValueChange = { s ->
                    saturation = s
                    updateColor()
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
            Text("${(saturation * 100).roundToInt()}%", style = TextStyle(fontSize = 11.sp), modifier = Modifier.width(40.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Val", modifier = Modifier.width(40.dp))
            Slider(
                value = value,
                onValueChange = { v ->
                    value = v
                    updateColor()
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
            Text("${(value * 100).roundToInt()}%", style = TextStyle(fontSize = 11.sp), modifier = Modifier.width(40.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Alph", modifier = Modifier.width(40.dp))
            Slider(
                value = alpha,
                onValueChange = { a ->
                    alpha = a
                    updateColor()
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
            Text("${(alpha * 100).roundToInt()}%", style = TextStyle(fontSize = 11.sp), modifier = Modifier.width(40.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("R", modifier = Modifier.width(20.dp))
            Text("${(color.red * 255).roundToInt()}", modifier = Modifier.weight(1f))
            Text("G", modifier = Modifier.width(20.dp))
            Text("${(color.green * 255).roundToInt()}", modifier = Modifier.weight(1f))
            Text("B", modifier = Modifier.width(20.dp))
            Text("${(color.blue * 255).roundToInt()}", modifier = Modifier.weight(1f))
        }

        TextField(
            value = hexInput,
            onValueChange = { hexInput = it },
            label = { Text("Hex") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 11.sp),
            isError = hexInput.toComposeColor() == null && hexInput.isNotEmpty()
        )

        Button(
            onClick = {
                hexInput.toComposeColor()?.let {
                    onColorChange(it)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Hex")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color)
                .border(1.dp, Color.Gray)
        )
    }
}

@Composable
fun ColorPickerDialog(
    color: Color,
    onColorChange: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableStateOf(color) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Color") },
        text = {
            ColorPickerPanel(
                color = currentColor,
                onColorChange = { currentColor = it }
            )
        },
        confirmButton = {
            Button(onClick = {
                onColorChange(currentColor)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun Color.toHex(): String {
    val r = (red * 255).roundToInt().toString(16).padStart(2, '0')
    val g = (green * 255).roundToInt().toString(16).padStart(2, '0')
    val b = (blue * 255).roundToInt().toString(16).padStart(2, '0')
    val a = (alpha * 255).roundToInt().toString(16).padStart(2, '0')
    return "#$r$g$b$a".uppercase()
}

fun String.toComposeColor(): Color? {
    return try {
        val hex = this.removePrefix("#").uppercase()
        when (hex.length) {
            6 -> {
                val r = hex.substring(0, 2).toInt(16) / 255f
                val g = hex.substring(2, 4).toInt(16) / 255f
                val b = hex.substring(4, 6).toInt(16) / 255f
                Color(red = r, green = g, blue = b)
            }
            8 -> {
                val r = hex.substring(0, 2).toInt(16) / 255f
                val g = hex.substring(2, 4).toInt(16) / 255f
                val b = hex.substring(4, 6).toInt(16) / 255f
                val a = hex.substring(6, 8).toInt(16) / 255f
                Color(red = r, green = g, blue = b, alpha = a)
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

fun Color.toHsv(): Triple<Float, Float, Float> {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val hue = when {
        delta == 0f -> 0f
        max == r -> (60f * (((g - b) / delta) % 6f) + 360f) % 360f
        max == g -> (60f * (((b - r) / delta) + 2f))
        else -> (60f * (((r - g) / delta) + 4f))
    }

    val saturation = if (max == 0f) 0f else delta / max
    val value = max

    return Triple(hue, saturation, value)
}

fun hsvToColor(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {
    val h = hue % 360f
    val c = value * saturation
    val hPrime = h / 60f
    val x = c * (1f - abs(hPrime % 2f - 1f))
    val m = value - c

    val (r, g, b) = when {
        hPrime < 1f -> Triple(c, x, 0f)
        hPrime < 2f -> Triple(x, c, 0f)
        hPrime < 3f -> Triple(0f, c, x)
        hPrime < 4f -> Triple(0f, x, c)
        hPrime < 5f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(red = r + m, green = g + m, blue = b + m, alpha = alpha)
}
