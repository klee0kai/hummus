package com.github.klee0kai.hummus.storybook.previews

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.github.klee0kai.hummus.compose.components.dropdownfields.DropDownField
import com.github.klee0kai.hummus.compose.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.hummus.compose.debug.DebugContentPreview
import com.github.klee0kai.hummus.compose.debug.DebugScreenPreview
import com.github.klee0kai.hummus.design.core.DebugOnly


@OptIn(DebugOnly::class)
@Composable
@Preview
fun DropDownFieldPreview() = DebugScreenPreview {
    var isExpanded by remember { mutableStateOf(false) }
    val variants = remember {
        buildList {
            repeat(10) { idx ->
                add("select $idx")
            }
        }
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        DropDownField(
            expanded = isExpanded,
            variants = variants,
            selectedIndex = selectedIndex,
            onExpandedChange = { isExpanded = it },
            onSelected = {
                isExpanded = false
                selectedIndex = it
            },
            label = {
                Text(text = "select")
            }
        )
    }
}


@Preview
@DebugOnly
@Composable
fun SimpleSelectPopupMenuPreview() = DebugContentPreview {
    SimpleSelectPopupMenu(
        variants = listOf(
            LoremIpsum(2).values.joinToString { it },
            LoremIpsum(4).values.joinToString { it }
        )
    )
}
