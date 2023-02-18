package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.interfaces.IEq;

import java.util.Objects;

public class Equals {

    public static <T> IEq<T> objectEq() {
        return Objects::equals;
    }

    public static <T> IEq<T> linkEq() {
        return (it1, it2) -> it1 == it2;
    }

    public static <T> IEq<T> typeEq() {
        return (it1, it2) -> it1 != null && it2 != null && Objects.equals(it1.getClass(), it2.getClass())
                || it1 == null && it2 == null;
    }

}
