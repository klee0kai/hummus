package com.github.klee0kai.android_devkit.collections.gen;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.interfaces.IGroup;
import com.github.klee0kai.android_devkit.model.Group;

import java.util.Collections;
import java.util.List;

/**
 * simple way to group to {@link com.github.klee0kai.android_devkit.model.Group} model
 */
public class GroupsToGroups {

    public interface IGroupLongSimple<Key, T> {
        Key groupId(T it);
    }


    public static <Key, T> IGroup<Key, T, Group<Key, T>> simple(IGroupLongSimple<Key, T> group) {
        return new IGroup<Key, T, Group<Key, T>>() {

            @Override
            public Key groupId(T it) {
                return group.groupId(it);
            }

            @Override
            public List<Group<Key, T>> group(List<T> lGroup) {
                if (ListUtils.isEmpty(lGroup))
                    return Collections.emptyList();
                Group<Key, T> it = new Group<>();
                it.key = groupId(lGroup.get(0));
                it.items = lGroup;
                return Collections.singletonList(it);
            }
        };
    }


    public static <T> IGroup<Class<T>, T, Group<Class<T>, T>> byType() {
        return new IGroup<Class<T>, T, Group<Class<T>, T>>() {

            @Override
            public Class<T> groupId(T it) {
                return it != null ? (Class<T>) it.getClass() : null;
            }

            @Override
            public List<Group<Class<T>, T>> group(List<T> lGroup) {
                if (ListUtils.isEmpty(lGroup))
                    return Collections.emptyList();
                Group<Class<T>, T> it = new Group<>();
                it.key = groupId(lGroup.get(0));
                it.items = lGroup;
                return Collections.singletonList(it);
            }
        };
    }


}
