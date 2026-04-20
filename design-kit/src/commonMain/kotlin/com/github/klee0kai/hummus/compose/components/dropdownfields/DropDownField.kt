@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.hummus.compose.components.dropdownfields

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.utils.possitions.onGlobalPositionState
import com.github.klee0kai.hummus.compose.utils.possitions.rememberViewPosition
import com.github.klee0kai.hummus.compose.components.overlay.PopupMenu
import com.github.klee0kai.hummus.design.core.DesignComponent
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun DropDownField(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    isSkeleton: Boolean = false,
    variants: List<String> = emptyList(),
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onSelected: (Int) -> Unit = {},
    label: (@Composable () -> Unit)? = null,
) {
    val theme = LocalHummusTheme.current
    val textFieldPosition = rememberViewPosition()
    val textFieldInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        textFieldInteractionSource.interactions
            .filterIsInstance<PressInteraction.Press>()
            .collect {
                onExpandedChange(!expanded)
            }
    }

    HummusTextField(
        modifier = modifier
            .onGlobalPositionState(textFieldPosition),
        isSkeleton = isSkeleton,
        enabled = variants.isNotEmpty(),
        interactionSource = textFieldInteractionSource,
        readOnly = true,
        singleLine = true,
        value = variants.getOrNull(selectedIndex) ?: "",
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        onValueChange = { },
        label = label,
    )

    PopupMenu(
        visible = expanded && variants.isNotEmpty(),
        positionAnchor = textFieldPosition,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        SimpleSelectPopupMenu(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            variants = variants,
            onSelected = { _, idx -> onSelected(idx) },
        )
    }

}

