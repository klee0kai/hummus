package com.github.klee0kai.android_devkit.collections.weaklist;

import com.github.klee0kai.android_devkit.model.IProvide;
import com.github.klee0kai.android_devkit.model.RefProvide;

import java.util.Collection;

public class WeakList<T> extends RefList<T> {

    public WeakList() {

    }

    public WeakList(Collection<? extends T> c) {
        this();
        addAll(c);
    }


    @Override
    IProvide<T> wrapRef(T val) {
        return RefProvide.weak(val);
    }
}
