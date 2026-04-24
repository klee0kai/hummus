package com.github.klee0kai.hummus.storybook.di.providers

import com.github.klee0kai.hummus.storybook.ui.main.viewmodel.AppBarViewModel

interface PresenterProvider {

    fun appBarViewModel(): AppBarViewModel

}