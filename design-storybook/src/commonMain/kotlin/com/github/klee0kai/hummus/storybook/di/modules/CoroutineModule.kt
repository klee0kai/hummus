package com.github.klee0kai.hummus.storybook.di.modules

import com.github.klee0kai.hummus.coroutine.SafeContextScope
import com.github.klee0kai.hummus.storybook.di.DefaultDispatcher
import com.github.klee0kai.hummus.storybook.di.MainDispatcher
import com.github.klee0kai.stone.annotations.module.Module
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
interface CoroutineModule {

    @MainDispatcher
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @DefaultDispatcher
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @MainDispatcher
    fun mainThreadScope(
        @MainDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

    @DefaultDispatcher
    fun defaultThreadScope(
        @DefaultDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

}
