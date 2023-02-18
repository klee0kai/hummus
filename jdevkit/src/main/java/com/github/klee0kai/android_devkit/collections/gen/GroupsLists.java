package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.interfaces.IGroup;

import java.util.Collections;
import java.util.List;

/**
 * simple way to group to sub lists
 */
public class GroupsLists {

    public interface IGroupLongSimple<Key, T> {
        Key groupId(T it);
    }


    public static <Key, T> IGroup<Key, T, List<T>> simple(IGroupLongSimple<Key, T> group) {
        return new IGroup<Key, T, List<T>>() {

            @Override
            public Key groupId(T it) {
                return group.groupId(it);
            }

            @Override
            public List<List<T>> group(List<T> lGroup) {
                if (ListUtils.isEmpty(lGroup))
                    return Collections.emptyList();
                return Collections.singletonList(lGroup);
            }
        };
    }


    public static <T> IGroup<Class<T>, T, List<T>> byType() {
        return new IGroup<Class<T>, T, List<T>>() {

            @Override
            public Class<T> groupId(T it) {
                return it != null ? (Class<T>) it.getClass() : null;
            }

            @Override
            public List<List<T>> group(List<T> lGroup) {
                if (ListUtils.isEmpty(lGroup))
                    return Collections.emptyList();
                return Collections.singletonList(lGroup);
            }
        };
    }


}
