package com.github.klee0kai.hummus.storybook.ui.components_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.utils.views.currentRef
import com.github.klee0kai.hummus.design.core.ComponentParameter
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.github.klee0kai.hummus.storybook.Screen
import com.github.klee0kai.hummus.storybook.navigation.ComponentEditDestination
import com.github.klee0kai.hummus.storybook.ui.utils.getRegisteredDesignComponents

@Composable
fun ComponentListScreen(
    selectedComponent: DesignComponentMethod? = null,
) = Screen("Components") {
    val router by LocalHummusRouter.currentRef
    var parameterValues by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Get components from registry - may be empty in non-debug builds
    val components = getRegisteredDesignComponents()
    val filteredComponents = components.filter { component ->
        component.methodName.contains(searchQuery.text, ignoreCase = true) ||
                component.pkg.contains(searchQuery.text, ignoreCase = true)
    }

    ComponentListPanel(
        components = filteredComponents,
        selectedComponent = selectedComponent,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onComponentSelected = { component ->
            router?.navigate(destination = ComponentEditDestination(component))
            // Initialize with default values
            parameterValues = component.parameters.associate { param ->
                param.name to getDefaultValueForParameter(param)
            }
        },
    )

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

fun getDefaultValueForParameter(param: ComponentParameter): Any? {
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
