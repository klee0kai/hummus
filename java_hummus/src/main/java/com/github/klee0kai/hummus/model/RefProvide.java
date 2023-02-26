package com.github.klee0kai.hummus.model;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class RefProvide<T> implements IProvide<T> {

    private final Reference<T> ref;

    public static <T> RefProvide<T> weak(T val) {
        return new RefProvide<>(new WeakReference<>(val));
    }

    public static <T> RefProvide<T> soft(T val) {
        return new RefProvide<>(new SoftReference<>(val));
    }

    public RefProvide(Reference<T> ref) {
        this.ref = ref;
    }

    @Override
    public T get() {
        return ref != null ? ref.get() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefProvide<?> that = (RefProvide<?>) o;
        return Objects.equals(get(), that.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get());
    }

    @Override
    public String toString() {
        return "WeakProvide{" +
                "ref=" + get() +
                '}';
    }
}
