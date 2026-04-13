package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.SameModel

object Equals {

    fun <T1, T2> objectEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1 == it2
    }

    fun <T1, T2> linkEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1 === it2
    }

    fun <T1 : Any, T2 : Any> typeEq(): (T1, T2) -> Boolean = { it1, it2 ->
        it1.javaClass === it2.javaClass
    }


    fun <T1, T2> sameOrEq(): (T1, T2) -> Boolean = { it1, it2 ->
        when {
            it1 === it2 -> true
            it2 == null -> false
            else -> ((it1 as? SameModel)?.isSame(it2) ?: false) || it1 == it2
        }
    }


}