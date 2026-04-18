package com.github.klee0kai.hummus.compose.components.dropdownfields

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.klee0kai.hummus.compose.LocalHummusTheme
import com.github.klee0kai.hummus.compose.utils.possitions.onGlobalPositionState
import com.github.klee0kai.hummus.compose.utils.possitions.pxToDp
import com.github.klee0kai.hummus.compose.utils.possitions.rememberViewPosition

@Composable
fun SimpleSelectPopupMenu(
    modifier: Modifier = Modifier,
    surface: Color = LocalHummusTheme.current.colorScheme.popupMenu.surfaceColor,
    variants: List<String> = emptyList(),
    onSelected: (variant: String, index: Int) -> Unit = { _, _ -> },
) {

    if (variants.isEmpty()) return
    val container = rememberViewPosition()

    LazyColumn(
        modifier = modifier
            .onGlobalPositionState(container)
            .heightIn(0.dp, 200.dp)
            .background(color = surface, shape = RoundedCornerShape(16.dp)),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        variants.forEachIndexed { inx, text ->
            item {
                Text(
                    text = text,
                    modifier = Modifier
                        .clickable { onSelected.invoke(text, inx) }
                        .defaultMinSize(minWidth = container.value?.size?.width?.pxToDp() ?: 200.dp)
                        .padding(all = 12.dp)
                        .padding(start = 8.dp, end = 28.dp)

                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


