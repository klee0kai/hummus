package com.github.klee0kai.android_devkit.collections.interfaces;

import org.jetbrains.annotations.Nullable;

public interface IJoin<T1, T2, TOut> {

    boolean isJoin(T1 it1, T2 it2);

    TOut join(@Nullable T1 it1, @Nullable T2 it2);

}
