package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.IdItemModel

object GroupKeys {

    fun <Type : Any> typeKey(): (Type) -> Class<Type> = { it ->
        it.javaClass
    }

    fun <Type : IdItemModel<IdType>, IdType> idKey(): (Type) -> IdType = { it ->
        it.id
    }

}