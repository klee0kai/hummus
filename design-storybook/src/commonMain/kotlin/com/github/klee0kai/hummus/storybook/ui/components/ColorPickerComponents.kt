package com.github.klee0kai.hummus.storybook.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

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
fun MultiColorWheel(
    colors: List<Pair<String, Color>>,
    selectedIndices: Set<Int>,
    onColorChange: (Int, Float, Float) -> Unit,
    value: Float = 1f,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(280.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val x = change.position.x - 140.dp.toPx()
                        val y = change.position.y - 140.dp.toPx()
                        val distance = sqrt(x * x + y * y)
                        val radius = 120.dp.toPx()

                        if (distance <= radius) {
                            val angle = (atan2(y, x) * 180 / 3.14 + 360) % 360
                            val sat = (distance / radius).coerceIn(0f, 1f)

                            selectedIndices.forEach { index ->
                                onColorChange(index, angle.toFloat(), sat)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(280.dp)) {
            drawColorWheel(hue = 0f, saturation = 1f, value = value)

            val wheelRadius = 120.dp.toPx()
            val markerColors = listOf(
                Color.Red, Color.Green, Color.Blue, Color.Yellow,
                Color.Cyan, Color.Magenta
            )

            selectedIndices.forEachIndexed { displayIndex, colorIndex ->
                val hsv = colors[colorIndex].second.toHsv()
                val hue = hsv.first
                val sat = hsv.second

                val angleRad = (hue * 3.14 / 180f).toFloat()
                val markerX = (sat * wheelRadius * cos(angleRad)).toFloat()
                val markerY = (sat * wheelRadius * sin(angleRad)).toFloat()

                val markerColor = markerColors[displayIndex % markerColors.size]

                drawCircle(
                    color = Color.White,
                    radius = 10.dp.toPx(),
                    center = center.copy(x = center.x + markerX, y = center.y + markerY)
                )
                drawCircle(
                    color = markerColor,
                    radius = 7.dp.toPx(),
                    center = center.copy(x = center.x + markerX, y = center.y + markerY)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 5.dp.toPx(),
                    center = center.copy(x = center.x + markerX, y = center.y + markerY)
                )
            }
        }
    }
}

@Composable
fun ColorWheel(
    hue: Float,
    saturation: Float,
    value: Float,
    onHueChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDraggingWheel by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(240.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val x = change.position.x - 120.dp.toPx()
                        val y = change.position.y - 120.dp.toPx()
                        val distance = sqrt(x * x + y * y)
                        val radius = 120.dp.toPx()

                        if (distance <= radius) {
                            val angle = (atan2(y, x) * 180 / 3.14 + 360) % 360
                            val sat = (distance / radius).coerceIn(0f, 1f)
                            onHueChange(angle.toFloat())
                            onSaturationChange(sat)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(240.dp)) {
            drawColorWheel(hue = hue, saturation = saturation, value = value)

            val wheelRadius = 120.dp.toPx()
            val angleRad = (hue * 3.14 / 180f).toFloat()
            val x = (saturation * wheelRadius * cos(angleRad)).toFloat()
            val y = (saturation * wheelRadius * sin(angleRad)).toFloat()

            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = center.copy(x = center.x + x, y = center.y + y)
            )
            drawCircle(
                color = Color.Black,
                radius = 6.dp.toPx(),
                center = center.copy(x = center.x + x, y = center.y + y)
            )
        }
    }
}

fun DrawScope.drawColorWheel(hue: Float, saturation: Float, value: Float) {
    val wheelRadius = 120.dp.toPx()
    val centerX = center.x
    val centerY = center.y
    val steps = 360

    for (i in 0 until steps) {
        val angle1 = i * 360f / steps
        val angle2 = (i + 1) * 360f / steps

        for (sat in 0..100 step 2) {
            val satNorm = sat / 100f
            val innerRadius = (satNorm - 0.02f) * wheelRadius
            val outerRadius = satNorm * wheelRadius

            val startAngle = angle1
            val sweepAngle = angle2 - angle1

            val color = hsvToColor(angle1, satNorm, value)
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(
                    centerX - outerRadius,
                    centerY - outerRadius
                ),
                size = Size(outerRadius * 2, outerRadius * 2)
            )
        }
    }

    drawCircle(
        color = Color.White,
        radius = 3.dp.toPx(),
        center = center
    )
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
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Color Wheel", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold))
                ColorWheel(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onHueChange = { h ->
                        hue = h
                        updateColor()
                    },
                    onSaturationChange = { s ->
                        saturation = s
                        updateColor()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Controls", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Hue", modifier = Modifier.width(30.dp), style = TextStyle(fontSize = 10.sp))
                    Slider(
                        value = hue,
                        onValueChange = { h ->
                            hue = h
                            updateColor()
                        },
                        valueRange = 0f..360f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${hue.roundToInt()}°", style = TextStyle(fontSize = 9.sp), modifier = Modifier.width(35.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Sat", modifier = Modifier.width(25.dp), style = TextStyle(fontSize = 10.sp))
                    Slider(
                        value = saturation,
                        onValueChange = { s ->
                            saturation = s
                            updateColor()
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(saturation * 100).roundToInt()}%", style = TextStyle(fontSize = 9.sp), modifier = Modifier.width(30.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Val", modifier = Modifier.width(25.dp), style = TextStyle(fontSize = 10.sp))
                    Slider(
                        value = value,
                        onValueChange = { v ->
                            value = v
                            updateColor()
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(value * 100).roundToInt()}%", style = TextStyle(fontSize = 9.sp), modifier = Modifier.width(30.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Alph", modifier = Modifier.width(25.dp), style = TextStyle(fontSize = 10.sp))
                    Slider(
                        value = alpha,
                        onValueChange = { a ->
                            alpha = a
                            updateColor()
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(alpha * 100).roundToInt()}%", style = TextStyle(fontSize = 9.sp), modifier = Modifier.width(30.dp))
                }
            }
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

@Composable
fun MultiColorPickerDialog(
    colors: List<Pair<String, Color>>,
    onColorsChange: (List<Pair<String, Color>>) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedIndices by remember { mutableStateOf(setOf(0)) }
    var updatedColors by remember { mutableStateOf(colors) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Multiple Colors") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Select colors to edit:", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    updatedColors.forEachIndexed { index, (name, color) ->
                        Column(
                            modifier = Modifier
                                .clickable {
                                    selectedIndices = if (selectedIndices.contains(index)) {
                                        selectedIndices - index
                                    } else {
                                        selectedIndices + index
                                    }
                                }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color)
                                    .border(
                                        width = if (selectedIndices.contains(index)) 3.dp else 1.dp,
                                        color = if (selectedIndices.contains(index)) Color.White else Color.Gray
                                    )
                            )
                            Text(name, style = TextStyle(fontSize = 8.sp), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }

                Text("Adjust on the wheel (multiple markers):", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold))

                MultiColorWheel(
                    colors = updatedColors,
                    selectedIndices = selectedIndices,
                    onColorChange = { colorIndex, hue, sat ->
                        val hsv = updatedColors[colorIndex].second.toHsv()
                        updatedColors = updatedColors.mapIndexed { index, (name, _) ->
                            if (index == colorIndex) {
                                name to hsvToColor(hue, sat, hsv.third)
                            } else {
                                name to updatedColors[index].second
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (selectedIndices.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Selected:", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        selectedIndices.forEach { index ->
                            Text(
                                updatedColors[index].first,
                                style = TextStyle(fontSize = 10.sp),
                                modifier = Modifier
                                    .background(Color.Gray.copy(alpha = 0.3f))
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onColorsChange(updatedColors)
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

@Composable
fun MultiColorComponentSelector(
    label: String,
    colors: List<Pair<String, Color>>,
    onColorsChange: (List<Pair<String, Color>>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndices by remember { mutableStateOf(setOf<Int>()) }
    var showColorPicker by remember { mutableStateOf(false) }

    if (showColorPicker && selectedIndices.isNotEmpty()) {
        val firstSelectedColor = colors[selectedIndices.first()].second
        ColorPickerDialog(firstSelectedColor, { newColor ->
            val updated = colors.mapIndexed { index, (name, color) ->
                if (selectedIndices.contains(index)) name to newColor else name to color
            }
            onColorsChange(updated)
        }, onDismiss = { showColorPicker = false })
    }

    Column(modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold))
            Button(
                onClick = { showColorPicker = true },
                enabled = selectedIndices.isNotEmpty(),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Apply Color", style = TextStyle(fontSize = 10.sp))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            colors.forEachIndexed { index, (name, color) ->
                Column(
                    modifier = Modifier.clickable {
                        selectedIndices = if (selectedIndices.contains(index)) {
                            selectedIndices - index
                        } else {
                            selectedIndices + index
                        }
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color)
                            .border(
                                width = if (selectedIndices.contains(index)) 3.dp else 1.dp,
                                color = if (selectedIndices.contains(index)) Color.White else Color.Gray
                            )
                    )
                    Text(name, style = TextStyle(fontSize = 9.sp), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
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
