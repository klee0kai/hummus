package com.github.klee0kai.hummus.storybook.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.klee0kai.hummus.compose.HummusTheme
import com.github.klee0kai.hummus.compose.theme.HummusDefaultThemes
import com.github.klee0kai.storybook.components.TypeSchemeEditor
import com.github.klee0kai.storybook.components.TypographyPreview

@Composable
fun TypographyBrowserScreen(modifier: Modifier = Modifier) {
    var baseTheme by remember { mutableStateOf(HummusDefaultThemes.darkTheme) }
    var typeScheme by remember { mutableStateOf(baseTheme.typeScheme) }

    val previewTheme = baseTheme.copy(typeScheme = typeScheme)

    Row(modifier.fillMaxSize()) {
        com.github.klee0kai.hummus.compose.HummusTheme {
            TypeSchemeEditor(
                body = typeScheme.body,
                bodySmall = typeScheme.bodySmall,
                screenHeader = typeScheme.screenHeader,
                header = typeScheme.header,
                buttonText = typeScheme.buttonText,
                onBodyChange = { typeScheme = typeScheme.copy(body = it) },
                onBodySmallChange = { typeScheme = typeScheme.copy(bodySmall = it) },
                onScreenHeaderChange = { typeScheme = typeScheme.copy(screenHeader = it) },
                onHeaderChange = { typeScheme = typeScheme.copy(header = it) },
                onButtonTextChange = { typeScheme = typeScheme.copy(buttonText = it) },
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            )
        }

        TypographyPreview(
            theme = previewTheme,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
        )
    }
}
