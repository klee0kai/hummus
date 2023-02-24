package com.github.klee0kai.hummus.model

interface ICloneable : Cloneable {

    public override fun clone(): Any = super.clone()

}