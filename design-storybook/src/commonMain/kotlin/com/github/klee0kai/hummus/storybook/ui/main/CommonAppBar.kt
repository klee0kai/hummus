package com.github.klee0kai.hummus.storybook.ui.main

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.github.klee0kai.hummus.compose.components.appbar.HummusBarStates
import com.github.klee0kai.hummus.compose.components.appbar.LoadingTitle
import com.github.klee0kai.hummus.compose.components.appbar.SearchTitle
import com.github.klee0kai.hummus.compose.components.icons.BackMenuIcon
import com.github.klee0kai.hummus.compose.utils.views.collectAsState
import com.github.klee0kai.hummus.compose.utils.views.rememberClickDebounced
import com.github.klee0kai.hummus.compose.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.hummus.compose.utils.views.rememberTargetFaded
import com.github.klee0kai.hummus.storybook.di.StoryBookDI
import com.github.klee0kai.hummus.storybook.ui.main.viewmodel.AppBarStates
import kotlinx.coroutines.flow.map
import kotlin.time.Duration


@Composable
fun CommonAppBar() {
    val router = remember { StoryBookDI.appRouter() }
    val vm = remember { StoryBookDI.appBarViewModel() }
    val isLoadingState by vm.commonLoadingTrackFlow.map { it > 0 }
        .collectAsState(key = Unit, initial = false)

    var isSearchOpened by remember { mutableStateOf(false) }
    val searchText by vm.searchText.collectAsState(key = Unit, initial = "")
    val screenTitle by vm.titleText.collectAsState(key = Unit, initial = "")
    val searchFocusRequester = remember { FocusRequester() }


    val targetTitleId by rememberTargetFaded {
        when {
            isSearchOpened -> AppBarStates.Search
            isLoadingState -> AppBarStates.Loading
            else -> AppBarStates.Simple
        }
    }


    HummusBarStates(
        isVisible = true,
        modifier = Modifier,
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router.back() },
                content = { BackMenuIcon() },
            )
        },
        titleContent = {
            when (targetTitleId.current) {
                AppBarStates.Simple -> {
                    Text(
                        modifier = Modifier.alpha(targetTitleId.alpha),
                        text = screenTitle,
                    )
                }

                AppBarStates.Search -> {
                    SearchTitle(
                        searchTitle = "Search",
                        textModifier = Modifier
                            .focusRequester(searchFocusRequester),
                        searchText = searchText,
                        onSearch = rememberClickDebouncedArg(debounce = Duration.ZERO) { newText ->
                            vm.updateSearchText(newText)
                        },
                        onClose = rememberClickDebounced {
                            vm.updateSearchText("")
                            isSearchOpened = false
                        }
                    )
                }

                AppBarStates.Loading -> {
                    LoadingTitle(text = "Loading...")
                }
            }
        },
    )
}