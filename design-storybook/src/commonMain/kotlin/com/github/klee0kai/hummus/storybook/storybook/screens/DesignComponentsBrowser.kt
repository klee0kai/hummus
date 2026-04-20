package com.github.klee0kai.hummus.storybook.storybook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.design.core.ComponentParameter
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.github.klee0kai.hummus.design.core.ParameterType
import com.github.klee0kai.hummus.storybook.storybook.components.ParametersEditor
import com.github.klee0kai.hummus.storybook.storybook.examples.initializeComponentPresets
import com.github.klee0kai.hummus.storybook.storybook.utils.getRegisteredComponents

/**
 * Экран для просмотра и тестирования компонентов с редактированием параметров
 */
@Composable
fun DesignComponentsBrowser(modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        initializeComponentPresets()
    }

    var selectedComponent by remember { mutableStateOf<DesignComponentMethod?>(null) }
    var parameterValues by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Get components from registry - may be empty in non-debug builds
    val components = getRegisteredComponents()
    val filteredComponents = components.filter { component ->
        component.methodName.contains(searchQuery.text, ignoreCase = true) ||
                component.pkg.contains(searchQuery.text, ignoreCase = true)
    }

    Row(modifier = modifier.fillMaxSize()) {
        // Left panel: Component list
        ComponentListPanel(
            components = filteredComponents,
            selectedComponent = selectedComponent,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onComponentSelected = { component ->
                selectedComponent = component
                // Initialize with default values
                parameterValues = component.parameters.associate { param ->
                    param.name to getDefaultValueForParameter(param)
                }
            },
            modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
        )

        // Right panel: Component editor and preview
        if (selectedComponent != null) {
            ComponentEditorPanel(
                component = selectedComponent!!,
                parameterValues = parameterValues,
                onParametersChange = { parameterValues = it },
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
            )
        } else {
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("Select a component to edit")
            }
        }
    }
}

@Composable
private fun ComponentListPanel(
    components: List<DesignComponentMethod>,
    selectedComponent: DesignComponentMethod?,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    onComponentSelected: (DesignComponentMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Search field
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            placeholder = { Text("Search components...") },
            textStyle = MaterialTheme.typography.bodySmall
        )

        Divider()

        // Component list
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(components.size) { index ->
                val component = components[index]
                ComponentListItem(
                    component = component,
                    isSelected = component == selectedComponent,
                    onClick = { onComponentSelected(component) }
                )
            }
        }
    }
}

@Composable
private fun ComponentListItem(
    component: DesignComponentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = component.methodName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = component.pkg,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "${component.parameters.size} params",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
    Divider()
}

@Composable
private fun ComponentEditorPanel(
    component: DesignComponentMethod,
    parameterValues: Map<String, Any?>,
    onParametersChange: (Map<String, Any?>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxHeight()) {
        // Editor
        ParametersEditor(
            component = component,
            values = parameterValues,
            onValuesChange = onParametersChange,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        )

        Divider(modifier = Modifier.fillMaxHeight(fraction = 1f).width(1.dp))

        // Preview
        ComponentPreviewPanel(
            component = component,
            parameterValues = parameterValues,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
private fun ComponentPreviewPanel(
    component: DesignComponentMethod,
    parameterValues: Map<String, Any?>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show component info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Component: ${component.methodName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Package: ${component.pkg}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Parameters: ${component.parameters.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Parameter values
        Text(
            text = "Current Parameters",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn {
            items(component.parameters.size) { index ->
                val param = component.parameters[index]
                val value = parameterValues[param.name]

                if (param.type is ParameterType.Nested) {
                    // no support
                    return@items
                }
                if ((param.type as? ParameterType.Generic)?.isFunction() == true) {
                    // no support
                    return@items
                }

                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "${param.name}:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = value?.toString() ?: "null",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Type: ${param.type.simpleNameString}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Divider()
            }
        }

        // Render preview if component has default parameters
        if (component.parameters.all { it.hasDefault }) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Component Render",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    component.invoke(
                        paramsBuilder = {
                            component.parameters.forEach { param ->
                                parameterValues[param.name]?.let { value ->
                                    set(param.name, value)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun getDefaultValueForParameter(param: ComponentParameter): Any? {
    return when {
        param.isBoolean() -> false
        param.isString() -> ""
        param.isInt() -> 0
        param.isFloat() -> 0f
        param.isDouble() -> 0.0
        param.isList() -> emptyList<String>()
        else -> null
    }
}
