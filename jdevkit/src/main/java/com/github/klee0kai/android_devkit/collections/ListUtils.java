package com.github.klee0kai.android_devkit.collections;

import com.github.klee0kai.android_devkit.collections.interfaces.IEq;
import com.github.klee0kai.android_devkit.collections.interfaces.IFilterIndexed;
import com.github.klee0kai.android_devkit.collections.interfaces.IGroup;
import com.github.klee0kai.android_devkit.collections.interfaces.IJoin;
import com.github.klee0kai.android_devkit.collections.interfaces.IMapIndexed;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListUtils {

    public static <T> List<T> filter(List<T> list, IFilterIndexed<T> filtHelper) {
        if (list == null) return null;
        LinkedList<T> out = new LinkedList<>();
        int i = 0;
        for (T it : list)
            if (filtHelper.filter(i++, it))
                out.add(it);
        return out;
    }

    public static <T> T first(List<T> list, IFilterIndexed<T> filtHelper) {
        if (list == null) return null;
        int i = 0;
        for (T it : list)
            if (filtHelper.filter(i++, it))
                return it;
        return null;
    }


    public static <T> int index(List<T> list, IFilterIndexed<T> filtHelper) {
        if (list == null) return -1;
        int i = 0;
        for (T it : list)
            if (filtHelper.filter(i++, it))
                return --i;
        return -1;
    }


    public static <T> boolean contains(List<T> list, IFilterIndexed<T> filtHelper) {
        return index(list, filtHelper) >= 0;
    }


    public static <T> List<T> removeDoubles(List<T> list, IEq<T> eqHelper) {
        if (list == null) return null;
        LinkedList<T> out = new LinkedList<>();
        for (T item : list) {
            boolean contains = ListUtils.contains(out, (i, it) -> eqHelper.eq(item, it));
            if (!contains)
                out.add(item);
        }
        return out;
    }

    public static <T, TOut> List<TOut> map(List<T> list, IMapIndexed<T, TOut> mapHelper) {
        if (list == null) return null;
        List<TOut> out = (list instanceof ArrayList) ? new ArrayList<>(list.size()) : new LinkedList<>();
        int i = 0;
        for (T it : list)
            out.add(mapHelper.map(i++, it));
        return out;
    }


    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }


    public static <Key, T, TOut> List<TOut> group(List<T> list, IGroup<Key, T, TOut> groupHelper) {
        if (list == null) return null;
        LinkedList<Key> keys = new LinkedList<>();
        Map<Key, LinkedList<T>> groups = new HashMap<>();
        for (T it : list) {
            Key groupId = groupHelper.groupId(it);
            LinkedList<T> gr = groups.get(groupId);
            if (gr == null) {
                gr = new LinkedList<>();
                groups.put(groupId, gr);
                keys.add(groupId);
            }
            gr.add(it);
        }

        List<TOut> out = new LinkedList<>();
        for (Key groupKey : keys) {
            out.addAll(groupHelper.group(groups.get(groupKey)));
        }
        return out;
    }

    public static <T1, T2, TOut> List<TOut> leftJoin(
            List<T1> l1,
            List<T2> l2,
            IJoin<T1, T2, TOut> joinHelper
    ) {
        if (l1 == null) return null;
        LinkedList<TOut> out = new LinkedList<>();
        for (T1 it1 : l1) {
            if (it1 == null) continue;
            boolean added = false;
            if (l2 != null)
                for (T2 it2 : l2)
                    if (it2 != null && joinHelper.isJoin(it1, it2)) {
                        out.add(joinHelper.join(it1, it2));
                        added = true;
                        break;
                    }
            if (!added)
                out.add(joinHelper.join(it1, null));
        }
        return out;
    }

    public static <T1, T2, TOut> List<TOut> rightJoin(
            List<T1> l1,
            List<T2> l2,
            IJoin<T1, T2, TOut> joinHelper
    ) {
        return leftJoin(l2, l1, new IJoin<T2, T1, TOut>() {
            @Override
            public boolean isJoin(T2 it1, T1 it2) {
                return joinHelper.isJoin(it2, it1);
            }

            @Override
            public TOut join(@Nullable T2 it1, @Nullable T1 it2) {
                return joinHelper.join(it2, it1);
            }
        });
    }

    public static <T1, T2, TOut> List<TOut> innerJoin(
            List<T1> l1,
            List<T2> l2,
            boolean multiToMulti,
            IJoin<T1, T2, TOut> joinHelper
    ) {
        if (l1 == null || l2 == null) return null;
        LinkedList<TOut> out = new LinkedList<>();
        for (T1 it1 : l1) {
            if (it1 == null) continue;
            for (T2 it2 : l2)
                if (it2 != null && joinHelper.isJoin(it1, it2)) {
                    out.add(joinHelper.join(it1, it2));
                    if (!multiToMulti) break;
                }
        }
        return out;
    }
}
