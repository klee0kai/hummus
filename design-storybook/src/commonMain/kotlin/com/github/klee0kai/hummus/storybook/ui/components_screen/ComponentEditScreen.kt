package com.github.klee0kai.hummus.storybook.ui.components_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.github.klee0kai.hummus.design.core.ParameterType
import com.github.klee0kai.hummus.storybook.Screen
import com.github.klee0kai.hummus.storybook.navigation.ComponentEditDestination
import com.github.klee0kai.hummus.storybook.ui.components.ParametersEditor

@Composable
fun ComponentEditScreen(
    destination: ComponentEditDestination = ComponentEditDestination(),
) = Screen(destination.component?.methodName ?: "") {
    if (destination.component == null) return@Screen
    var parameterValues by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }

    ComponentEditorPanel(
        component = destination.component,
        parameterValues = parameterValues,
        onParametersChange = { parameterValues = it },
        modifier = Modifier
            .fillMaxHeight()
    )

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

