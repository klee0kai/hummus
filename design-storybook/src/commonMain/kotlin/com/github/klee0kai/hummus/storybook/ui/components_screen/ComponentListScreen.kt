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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.LocalHummusRouter
import com.github.klee0kai.hummus.compose.utils.views.collectAsState
import com.github.klee0kai.hummus.compose.utils.views.currentRef
import com.github.klee0kai.hummus.design.core.ComponentParameter
import com.github.klee0kai.hummus.design.core.DesignComponentMethod
import com.github.klee0kai.hummus.storybook.Screen
import com.github.klee0kai.hummus.storybook.di.StoryBookDI
import com.github.klee0kai.hummus.storybook.navigation.ComponentEditDestination
import com.github.klee0kai.hummus.storybook.ui.utils.getRegisteredDesignComponents

@Composable
fun ComponentListScreen(
    selectedComponent: DesignComponentMethod? = null,
) = Screen("Components") {
    val router by LocalHummusRouter.currentRef
    val vm = remember { StoryBookDI.appBarViewModel() }
    var parameterValues by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }
    val searchText by vm.searchText.collectAsState(key = Unit, initial = "")


    // Get components from registry - may be empty in non-debug builds
    val components = getRegisteredDesignComponents()
    val filteredComponents = components.filter { component ->
        component.methodName.contains(searchText, ignoreCase = true) ||
                component.pkg.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        // Component list
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(filteredComponents.size) { index ->
                val component = filteredComponents[index]
                ComponentListItem(
                    component = component,
                    isSelected = component == selectedComponent,
                    onClick = {
                        router?.navigate(destination = ComponentEditDestination(component))
                        // Initialize with default values
                        parameterValues = component.parameters.associate { param ->
                            param.name to getDefaultValueForParameter(param)
                        }
                    }
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
