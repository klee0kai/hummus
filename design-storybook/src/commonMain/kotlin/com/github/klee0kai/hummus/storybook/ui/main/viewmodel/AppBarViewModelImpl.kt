package com.github.klee0kai.hummus.storybook.ui.main.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow

class AppBarViewModelImpl : AppBarViewModel {

    override val searchText = MutableStateFlow("")

    override val titleText = MutableStateFlow("Some Screen")

    override val commonLoadingTrackFlow = MutableStateFlow<Int>(0)

    override fun updateSearchText(text: String) {
        searchText.value = text
    }

    override fun setTitle(title: String) {
        titleText.value = title
    }

}