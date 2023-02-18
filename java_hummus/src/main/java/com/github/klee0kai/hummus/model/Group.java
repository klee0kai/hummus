package com.github.klee0kai.hummus.model;

import java.util.List;
import java.util.Objects;

public class Group<K, T> {

    public K key = null;

    public List<T> items = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group<?, ?> group = (Group<?, ?>) o;
        return Objects.equals(key, group.key) && Objects.equals(items, group.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, items);
    }
}
