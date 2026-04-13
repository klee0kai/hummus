package com.github.klee0kai.hummus.collections.gen

import com.github.klee0kai.hummus.model.IdItemModel
import kotlin.reflect.KClass

object GroupKeys {

    fun <Type : Any> typeKey(): (Type) -> KClass<out Type> = { it ->
        it::class
    }

    fun <Type : IdItemModel<IdType>, IdType> idKey(): (Type) -> IdType = { it ->
        it.id
    }

}
