package com.github.klee0kai.android_devkit.collections.gen

import com.github.klee0kai.android_devkit.model.IIdItem

object GroupKeys {

    fun <Type : Any> typeKey(): (Type) -> Class<Type> = { it ->
        it.javaClass
    }

    fun <Type : IIdItem<IdType>, IdType> idKey(): (Type) -> IdType = { it ->
        it.id
    }

}