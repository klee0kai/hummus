package com.github.klee0kai.android_devkit.collections.weaklist;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.gen.Maps;
import com.github.klee0kai.android_devkit.model.IProvide;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public abstract class RefList<T> implements List<T> {

    private final List<IProvide<T>> list = new LinkedList<>();

    abstract IProvide<T> wrapRef(T val);

    abstract RefList<T> createNew(List<T> list);


    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ListUtils.contains(
                list,
                ((idx, it) -> Objects.equals(it.get(), o))
        );
    }


    @Override
    public Object[] toArray() {
        return ListUtils.map(list, Maps.simple(IProvide::get)).toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return ListUtils.map(list, Maps.simple(IProvide::get)).toArray(a);
    }

    @Override
    public boolean add(T t) {
        clearNulls(null);
        return list.add(wrapRef(t));
    }

    @Override
    public boolean remove(Object o) {
        return clearNulls(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return toStrongList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        clearNulls(null);
        boolean added = false;
        for (T c : collection) {
            added |= list.add(wrapRef(c));
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        boolean added = false;
        for (T c : collection) {
            list.add(index++, wrapRef(c));
            added = true;
        }
        return added;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Iterator<IProvide<T>> it = list.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            IProvide<T> ref = it.next();
            if (ref == null || ref.get() == null
                    || collection != null && collection.contains(ref.get())) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Iterator<IProvide<T>> it = list.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            IProvide<T> ref = it.next();
            if (ref == null || ref.get() == null
                    || collection != null && !collection.contains(ref.get())) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        IProvide<T> ref = list.get(index);
        return ref != null ? ref.get() : null;
    }

    @Override
    public T set(int index, T element) {
        return list.set(index, wrapRef(element)).get();
    }

    @Override
    public void add(int index, T element) {
        list.add(index, wrapRef(element));
    }

    @Override
    public T remove(int index) {
        IProvide<T> ref = list.remove(index);
        return ref != null ? ref.get() : null;
    }

    @Override
    public int indexOf(Object o) {
        return ListUtils.index(list, (i, it) -> Objects.equals(o, it.get()));
    }

    @Override
    public int lastIndexOf(Object o) {
        return ListUtils.lastIndex(list, (i, it) -> Objects.equals(o, it.get()));
    }

    @Override
    public Iterator<T> iterator() {
        return new ListItr(list.listIterator());
    }


    @Override
    public ListIterator<T> listIterator() {
        return new ListItr(list.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListItr(list.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return createNew(toStrongList().subList(fromIndex, toIndex));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefList<?> refList = (RefList<?>) o;
        return Objects.equals(list, refList.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public List<T> toStrongList() {
        return ListUtils.map(list, Maps.simple(IProvide::get));
    }

    public boolean clearNulls() {
        return clearNulls(null);
    }

    private boolean clearNulls(Object item) {
        Iterator<IProvide<T>> it = list.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            IProvide<T> ref = it.next();
            if (ref == null || ref.get() == null || Objects.equals(ref.get(), item)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    private class ListItr implements ListIterator<T> {

        private final ListIterator<IProvide<T>> iterator;

        public ListItr(ListIterator<IProvide<T>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next().get();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public T previous() {
            return iterator.previous().get();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void set(T t) {
            iterator.set(wrapRef(t));
        }

        @Override
        public void add(T t) {
            iterator.add(wrapRef(t));
        }
    }

}
