package com.github.klee0kai.hummus.collections.interfaces;

public interface IJoin<T1, T2, TOut> {

    boolean isJoin(T1 it1, T2 it2);

    TOut join( T1 it1,  T2 it2);

}
