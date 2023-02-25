package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.IIdItemModel

object GroupKeys {

    fun <Type : Any> typeKey(): (Type) -> Class<Type> = { it ->
        it.javaClass
    }

    fun <Type : IIdItemModel<IdType>, IdType> idKey(): (Type) -> IdType = { it ->
        it.id
    }

}