package com.github.klee0kai.hummus.collections.weaklist;

import com.github.klee0kai.hummus.model.IProvide;
import com.github.klee0kai.hummus.model.RefProvide;

import java.util.Collection;
import java.util.List;

public class SoftList<T> extends RefList<T> {

    public SoftList() {

    }

    public SoftList(Collection<? extends T> c) {
        this();
        addAll(c);
    }


    @Override
    IProvide<T> wrapRef(T val) {
        return RefProvide.soft(val);
    }

    @Override
    RefList<T> createNew(List<T> list) {
        return new SoftList<>(list);
    }
}
