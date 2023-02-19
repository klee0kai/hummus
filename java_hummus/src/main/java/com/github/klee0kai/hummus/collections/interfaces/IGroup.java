package com.github.klee0kai.hummus.collections.interfaces;

import java.util.List;

public interface IGroup<Key,T, TOut> {

    Key groupId(T it);

    List<TOut> group(List<T> lGroup);

}
