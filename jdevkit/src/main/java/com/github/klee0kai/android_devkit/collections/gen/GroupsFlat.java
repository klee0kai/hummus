package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.interfaces.IGroup;

import java.util.List;

/**
 * simple way to group flat list
 */
public class GroupsFlat {

    public interface IGroupLongSimple<Key, T> {
        Key groupId(T it);
    }


    public static <Key, T> IGroup<Key, T, T> simple(IGroupLongSimple<Key, T> group) {
        return new IGroup<Key, T, T>() {

            @Override
            public Key groupId(T it) {
                return group.groupId(it);
            }

            @Override
            public List<T> group(List<T> lGroup) {
                return lGroup;
            }
        };
    }


    public static <T> IGroup<Class<T>, T, T> byType() {
        return new IGroup<Class<T>, T, T>() {

            @Override
            public Class<T> groupId(T it) {
                return it != null ? (Class<T>) it.getClass() : null;
            }

            @Override
            public List<T> group(List<T> lGroup) {
                return lGroup;
            }
        };
    }


}
