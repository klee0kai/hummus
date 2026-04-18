package com.github.klee0kai.storybook.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.design.core.*

/**
 * Компонент для ввода значения параметра с учетом его типа
 */
@Composable
fun ParameterInputField(
    parameter: ComponentParameter,
    value: Any?,
    onValueChange: (Any?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = parameter.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        when {
            parameter.isBoolean() -> BooleanInputField(
                value = value as? Boolean ?: false,
                onValueChange = onValueChange
            )
            parameter.isString() -> StringInputField(
                value = value as? String ?: "",
                onValueChange = onValueChange
            )
            parameter.isInt() -> IntInputField(
                value = value as? Int ?: 0,
                onValueChange = onValueChange
            )
            parameter.isFloat() -> FloatInputField(
                value = value as? Float ?: 0f,
                onValueChange = onValueChange
            )
            parameter.isDouble() -> DoubleInputField(
                value = value as? Double ?: 0.0,
                onValueChange = onValueChange
            )
            parameter.isList() -> ListInputField(
                value = value as? List<*>,
                onValueChange = onValueChange
            )
            else -> Text(
                text = "Type: ${parameter.type.simpleNameString}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun BooleanInputField(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(value.toString())
        Switch(
            checked = value,
            onCheckedChange = onValueChange
        )
    }
}

@Composable
private fun StringInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun IntInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    TextField(
        value = textValue,
        onValueChange = { text ->
            textValue = text
            text.toIntOrNull()?.let { onValueChange(it) }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun FloatInputField(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    TextField(
        value = textValue,
        onValueChange = { text ->
            textValue = text
            text.toFloatOrNull()?.let { onValueChange(it) }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun DoubleInputField(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    TextField(
        value = textValue,
        onValueChange = { text ->
            textValue = text
            text.toDoubleOrNull()?.let { onValueChange(it) }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun ListInputField(
    value: List<*>?,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textValue by remember {
        mutableStateOf(
            (value as? List<*>)?.joinToString(",") { it?.toString() ?: "" } ?: ""
        )
    }

    TextField(
        value = textValue,
        onValueChange = { text ->
            textValue = text
            val list = text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            onValueChange(list)
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = false,
        maxLines = 3,
        textStyle = MaterialTheme.typography.bodySmall,
        label = { Text("Enter values separated by comma") }
    )
}

/**
 * Компонент для выбора предподготовленного набора параметров
 */
@Composable
fun PresetSelector(
    component: DesignComponentMethod,
    selectedPreset: ParameterPreset?,
    onPresetSelected: (ParameterPreset?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val presets = ComponentPresetsRegistry.getPresets(component.methodName)?.presets ?: emptyList()

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Presets",
            style = MaterialTheme.typography.labelMedium
        )

        if (presets.isEmpty()) {
            Text(
                text = "No presets available",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            presets.forEach { preset ->
                Button(
                    onClick = { onPresetSelected(preset) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPreset == preset)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(preset.name, style = MaterialTheme.typography.labelMedium)
                        if (preset.description.isNotEmpty()) {
                            Text(
                                preset.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Компонент для редактирования всех параметров компонента
 */
@Composable
fun ParametersEditor(
    component: DesignComponentMethod,
    values: Map<String, Any?>,
    onValuesChange: (Map<String, Any?>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mutableValues = remember { mutableStateMapOf(*values.toList().toTypedArray()) }
    var selectedPreset by remember { mutableStateOf<ParameterPreset?>(null) }

    LaunchedEffect(selectedPreset) {
        selectedPreset?.let { preset ->
            mutableValues.clear()
            mutableValues.putAll(preset.values)
            onValuesChange(mutableValues.toMap())
        }
    }

    LaunchedEffect(mutableValues.toMap()) {
        onValuesChange(mutableValues.toMap())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Parameters",
            style = MaterialTheme.typography.headlineSmall
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Preset selector
        PresetSelector(
            component = component,
            selectedPreset = selectedPreset,
            onPresetSelected = { selectedPreset = it },
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Parameter inputs
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(component.parameters.size) { index ->
                val param = component.parameters[index]
                ParameterInputField(
                    parameter = param,
                    value = mutableValues[param.name],
                    onValueChange = { newValue ->
                        mutableValues[param.name] = newValue
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
