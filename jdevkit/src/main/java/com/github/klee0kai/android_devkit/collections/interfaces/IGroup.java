package com.github.klee0kai.android_devkit.collections.interfaces;

import java.util.List;

public interface IGroup<Key,T, TOut> {

    Key groupId(T it);

    List<TOut> group(List<T> lGroup);

}
