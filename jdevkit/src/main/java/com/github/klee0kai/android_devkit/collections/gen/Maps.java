package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.interfaces.IMapIndexed;

public class Maps {

    public interface IMap<T, TOut> {

        TOut map(T it);

    }

    static public <T, TOut> IMapIndexed<T, TOut> simple(IMap<T, TOut> map) {
        return (idx, it) -> map.map(it);
    }


}
