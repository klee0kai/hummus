package com.github.klee0kai.hummus.compose.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.github.klee0kai.hummus.compose.components.text.HummusTextField
import com.github.klee0kai.hummus.compose.utils.views.transparentColors

@Composable
fun SearchTitle(
    searchTitle: String,
    searchText: String,
    textModifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onClose: () -> Unit = {},
) {

    Box {
        HummusTextField(
            modifier = textModifier
                .wrapContentHeight()
                .fillMaxWidth(),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.4f),
                    text = searchTitle,
                )
            },
            value = searchText,
            onValueChange = { onSearch(it) },
            colors = TextFieldDefaults.transparentColors(),
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = { onClose.invoke() },
            content = { Icon(Icons.Filled.Close, contentDescription = null) }
        )
    }
}
