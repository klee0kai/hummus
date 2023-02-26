package com.github.klee0kai.hummus.arch.mvp

import com.github.klee0kai.hummus.collections.weaklist.WeakList
import com.github.klee0kai.hummus.threads.Threads
import com.github.klee0kai.hummus.threads.runMain
import com.github.klee0kai.hummus.threads.runMainDelayed

open class WeakViewsList : WeakList<IRefreshView?>() {

    fun refreshAllViews() {
        Threads.runMain { for (v in this) v?.refreshUI() }
    }

    fun refreshAllViews(delay: Int) {
        Threads.runMainDelayed(delay.toLong()) { for (v in this) v?.refreshUI() }
    }

}