package com.github.klee0kai.hummus

import timber.log.Timber

object AndroidDevKitLogs {

    var devKitLog: Timber.Tree? = Timber.tag("android_devkit")

    @JvmStatic
    fun main(args: Array<String>) {
    }

}