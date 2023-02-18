package com.github.klee0kai.android_devkit.model

interface IIdItem<IdType> : ISame {

    val id: IdType

    override fun isSame(o: Any?): Boolean {
        return this.javaClass == o?.javaClass
                && id == (o as? IIdItem<IdType>)?.id
    }

}