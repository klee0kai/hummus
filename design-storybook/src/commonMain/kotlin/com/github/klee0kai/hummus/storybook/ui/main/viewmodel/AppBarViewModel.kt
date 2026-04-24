package com.github.klee0kai.hummus.storybook.ui.main.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

interface AppBarViewModel {

    val searchText: Flow<String> get() = emptyFlow()

    val titleText: Flow<String> get() = emptyFlow()

    val commonLoadingTrackFlow: MutableStateFlow<Int> get() = MutableStateFlow(0)

    fun updateSearchText(text: String) = Unit

    fun setTitle(title: String) = Unit

}