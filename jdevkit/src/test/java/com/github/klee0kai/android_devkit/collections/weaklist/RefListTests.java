package com.github.klee0kai.android_devkit.collections.weaklist;

import static com.github.klee0kai.android_devkit.collections.weaklist.ChangeListsHelper.assertListsSame;
import static com.github.klee0kai.android_devkit.collections.weaklist.ChangeListsHelper.changeForEachListsSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RefListTests {

    @Test(timeout = 100)
    public void emptyList() {
        //When
        List<Integer> exampleList = new LinkedList<>();
        List<Integer> refList = new SoftList<>();


        //Then
        assertListsSame(exampleList, refList, new Integer[0]);
    }

    @Test(timeout = 100)
    public void initList() {
        //When
        List<Integer> exampleList = new LinkedList<>(Arrays.asList(1, 2, 4));
        List<Integer> refList = new SoftList<>(Arrays.asList(1, 2, 4));


        //Then
        assertListsSame(exampleList, refList, new Integer[0]);
    }

    @Test(timeout = 100)
    public void addOne() {
        //When
        List<Integer> exampleList = new LinkedList<>();
        List<Integer> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), arg -> arg.add(1));

        //Then
        assertListsSame(exampleList, refList, new Integer[0]);
    }

    @Test(timeout = 100)
    public void addFew() {
        //When
        List<Integer> exampleList = new LinkedList<>();
        List<Integer> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.add(1);
            return list.add(2);
        });

        //Then
        assertListsSame(exampleList, refList, new Integer[0]);
    }

    @Test(timeout = 100)
    public void addRemove() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.add(new ChangeListsHelper.SimpleIntPair(1, 2));
            list.add(new ChangeListsHelper.SimpleIntPair(2, 3));
            list.add(new ChangeListsHelper.SimpleIntPair(3, 4));

            return list.remove(2);
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void removeAll() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            return list.removeAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4)
            ));
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void nothingRemoveAll() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            return list.removeAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(7, 3),
                    new ChangeListsHelper.SimpleIntPair(7, 4)
            ));
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void clear() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            list.clear();
            return null;
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void retailAll() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            return list.retainAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4)
            ));
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void nothingRetailAll() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            return list.retainAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(7, 3),
                    new ChangeListsHelper.SimpleIntPair(7, 4)
            ));
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void removeByIndex() {
        //When
        List<ChangeListsHelper.SimpleIntPair> exampleList = new LinkedList<>();
        List<ChangeListsHelper.SimpleIntPair> refList = new SoftList<>();
        changeForEachListsSame(Arrays.asList(exampleList, refList), list -> {
            list.addAll(Arrays.asList(
                    new ChangeListsHelper.SimpleIntPair(1, 2),
                    new ChangeListsHelper.SimpleIntPair(1, 3),
                    new ChangeListsHelper.SimpleIntPair(1, 4),
                    new ChangeListsHelper.SimpleIntPair(1, 5),
                    new ChangeListsHelper.SimpleIntPair(2, 5),
                    new ChangeListsHelper.SimpleIntPair(3, 5),
                    new ChangeListsHelper.SimpleIntPair(4, 5)
            ));

            return list.remove(3);
        });

        //Then
        assertListsSame(exampleList, refList, new ChangeListsHelper.SimpleIntPair[0]);
    }


    @Test(timeout = 100)
    public void sameLists() {
        //When
        List<ChangeListsHelper.SimpleIntPair> ref1List = new SoftList<>();
        List<ChangeListsHelper.SimpleIntPair> ref2List = new SoftList<>();
        changeForEachListsSame(Arrays.asList(ref1List, ref2List), list -> {
            list.add(new ChangeListsHelper.SimpleIntPair(1, 2));
            list.add(new ChangeListsHelper.SimpleIntPair(2, 3));
            return list.add(new ChangeListsHelper.SimpleIntPair(3, 4));
        });

        //Then
        assertEquals(ref1List.hashCode(), ref2List.hashCode());
        assertEquals(ref1List, ref2List);
    }

    @Test(timeout = 100)
    public void diffLists() {
        //When
        List<ChangeListsHelper.SimpleIntPair> ref1List = new SoftList<>();
        List<ChangeListsHelper.SimpleIntPair> ref2List = new SoftList<>();
        changeForEachListsSame(Arrays.asList(ref1List, ref2List), list -> {
            list.add(new ChangeListsHelper.SimpleIntPair(1, 2));
            list.add(new ChangeListsHelper.SimpleIntPair(2, 3));
            return list.add(new ChangeListsHelper.SimpleIntPair(3, 4));
        });
        ref2List.add(new ChangeListsHelper.SimpleIntPair(3, 4));

        //Then
        assertNotEquals(ref1List.hashCode(), ref2List.hashCode());
        assertNotEquals(ref1List, ref2List);
    }


    @Test(timeout = 100)
    public void WeakItemsCollect() {
        //Given
        ChangeListsHelper.SimpleIntPair strongRef = new ChangeListsHelper.SimpleIntPair(7, 4);
        WeakList<ChangeListsHelper.SimpleIntPair> refList = new WeakList<>();
        refList.add(new ChangeListsHelper.SimpleIntPair(1, 2));
        refList.add(new ChangeListsHelper.SimpleIntPair(3, 4));
        refList.add(strongRef);
        refList.add(new ChangeListsHelper.SimpleIntPair(5, 4));

        //When
        System.gc();
        refList.clearNulls();

        //Then
        assertEquals(1, refList.size());
        assertEquals(strongRef, refList.get(0));
    }

}
