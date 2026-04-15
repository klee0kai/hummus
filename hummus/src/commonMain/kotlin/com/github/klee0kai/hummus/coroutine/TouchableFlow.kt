package com.github.klee0kai.hummus.coroutine

import kotlinx.coroutines.flow.Flow


interface TouchableFlow<out T, in Arg> : Flow<T> {

    /**
     * update data in cold flow
     */
    fun touch(arg: Arg)

}

fun <T> TouchableFlow<T, Unit>.touch() = touch(Unit)
