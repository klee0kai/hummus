package com.github.klee0kai.hummus.storybook.di.providers

import com.github.klee0kai.hummus.coroutine.SafeContextScope
import com.github.klee0kai.hummus.storybook.di.DefaultDispatcher
import com.github.klee0kai.hummus.storybook.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineProvider {

    @MainDispatcher
    fun mainDispatcher(): CoroutineDispatcher

    @MainDispatcher
    fun mainThreadScope(): SafeContextScope

    @DefaultDispatcher
    fun defaultDispatcher(): CoroutineDispatcher

    @DefaultDispatcher
    fun defaultThreadScope(): SafeContextScope

}