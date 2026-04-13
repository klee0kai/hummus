package com.github.klee0kai.hummus.model

interface IdItemModel<IdType> : SameModel {

    val id: IdType

    override fun isSame(o: Any): Boolean {
        return this::class == o::class
                && id == (o as? IdItemModel<IdType>)?.id
    }

}