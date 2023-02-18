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
import java.util.ListIterator;
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
        if (ListUtils.isEmpty(list)) return -1;
        int i = 0;
        for (T it : list) {
            if (filtHelper.filter(i++, it))
                return --i;
        }
        return -1;
    }


    public static <T> int lastIndex(List<T> list, IFilterIndexed<T> filtHelper) {
        if (ListUtils.isEmpty(list)) return -1;
        int i = list.size() - 1;
        ListIterator<T> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            if (filtHelper.filter(i--, it.previous()))
                return ++i;
        }
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
        for (T it : list) out.add(mapHelper.map(i++, it));
        return out;
    }

    public static <T> List<T> reversed(List<List<T>> list) {
        if (list == null) return null;
        List<T> out = new LinkedList<>();
        for (List<T> l : list) if (!ListUtils.isEmpty(l)) out.addAll(l);
        return out;
    }

    public static <T> List<T> flatten(List<List<T>> list) {
        if (list == null) return null;
        List<T> out = new LinkedList<>();
        for (List<T> l : list) if (!ListUtils.isEmpty(l)) out.addAll(l);
        return out;
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <Key, Type, OutType> List<OutType> group(List<Type> list, IGroup<Key, Type, OutType> groupHelper) {
        if (list == null) return null;
        LinkedList<Key> keys = new LinkedList<>();
        Map<Key, LinkedList<Type>> groups = new HashMap<>();
        for (Type it : list) {
            Key groupId = groupHelper.groupId(it);
            LinkedList<Type> gr = groups.get(groupId);
            if (gr == null) {
                gr = new LinkedList<>();
                groups.put(groupId, gr);
                keys.add(groupId);
            }
            gr.add(it);
        }

        List<OutType> out = new LinkedList<>();
        for (Key groupKey : keys) {
            List<OutType> g = groupHelper.group(groups.get(groupKey));
            if (!ListUtils.isEmpty(g)) out.addAll(g);
        }
        return out;
    }

    public static <T1, T2, OutType> List<OutType> leftJoin(
            List<T1> l1,
            List<T2> l2,
            IJoin<T1, T2, OutType> joinHelper
    ) {
        if (l1 == null) return null;
        LinkedList<OutType> out = new LinkedList<>();
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

    public static <T1, T2, OutType> List<OutType> innerJoin(
            List<T1> l1,
            List<T2> l2,
            boolean multiToMulti,
            IJoin<T1, T2, OutType> joinHelper
    ) {
        if (l1 == null || l2 == null) return null;
        LinkedList<OutType> out = new LinkedList<>();
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

    public static <T1, T2, OutType> List<OutType> fullOuterJoin(
            List<T1> l1,
            List<T2> l2,
            boolean multiToMulti,
            IJoin<T1, T2, OutType> joinHelper
    ) {
        if (l1 == null || l2 == null) return null;
        LinkedList<T1> l1Left = new LinkedList<>(l1);
        LinkedList<T2> l2Left = new LinkedList<>(l2);
        LinkedList<OutType> out = new LinkedList<>();
        for (T1 it1 : l1) {
            if (it1 == null) continue;
            for (T2 it2 : l2)
                if (it2 != null && joinHelper.isJoin(it1, it2)) {
                    out.add(joinHelper.join(it1, it2));
                    l2Left.remove(it2);
                    l1Left.remove(it1);
                    if (!multiToMulti) break;
                }
        }
        for (T1 it1 : l1Left) {
            out.add(joinHelper.join(it1, null));
        }
        for (T2 it2 : l2Left) {
            out.add(joinHelper.join(null, it2));
        }
        return out;
    }
}
