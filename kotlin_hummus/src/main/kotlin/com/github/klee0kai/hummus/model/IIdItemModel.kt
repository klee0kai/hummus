package com.github.klee0kai.hummus.model

interface IIdItemModel<IdType> : ISameModel {

    val id: IdType

    override fun isSame(o: Any?): Boolean {
        return this.javaClass == o?.javaClass
                && id == (o as? IIdItemModel<IdType>)?.id
    }

}