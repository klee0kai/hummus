package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.interfaces.IFilterIndexed;

import java.util.Objects;

public class Filters {

    public interface IFilterSimple<T> {
        boolean filter(T it);
    }

    public interface IIndexFilter {
        boolean filter(int index);
    }

    public static <T> IFilterIndexed<T> simple(IFilterSimple<T> simpleFilter) {
        return (idx, it) -> simpleFilter.filter(it);
    }


    public static <T> IFilterIndexed<T> byIndex(IIndexFilter indexFilter) {
        return (idx, it) -> indexFilter.filter(idx);
    }


    public static <T> IFilterIndexed<T> byType(Class<T> tClass) {
        return (idx, it) -> it != null && Objects.equals(it.getClass(), tClass);
    }


    public static <T> IFilterIndexed<T> sublist(int fromIndex) {
        return byIndex(index -> index >= fromIndex);
    }


    public static <T> IFilterIndexed<T> sublist(int fromIndex, int toIndex) {
        return byIndex(index -> index >= fromIndex && index <= toIndex);
    }

}
