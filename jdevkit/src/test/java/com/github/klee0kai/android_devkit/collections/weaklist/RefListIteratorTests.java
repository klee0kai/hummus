package com.github.klee0kai.android_devkit.collections.weaklist;

import static com.github.klee0kai.android_devkit.collections.weaklist.ChangeListsHelper.assertListsSame;
import static com.github.klee0kai.android_devkit.collections.weaklist.ChangeListsHelper.changeForEachListsSame;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class RefListIteratorTests {

    @Test(timeout = 100)
    public void removeByIterator() {
        //Given
        List<ChangeListsHelper.SimpleIntPair> originalList = Arrays.asList(
                new ChangeListsHelper.SimpleIntPair(1, 2),
                new ChangeListsHelper.SimpleIntPair(1, 3),
                new ChangeListsHelper.SimpleIntPair(1, 4),
                new ChangeListsHelper.SimpleIntPair(1, 5),
                new ChangeListsHelper.SimpleIntPair(2, 5),
                new ChangeListsHelper.SimpleIntPair(3, 5),
                new ChangeListsHelper.SimpleIntPair(4, 5)
        );
        List<ChangeListsHelper.SimpleIntPair> expList = new LinkedList<>(originalList);
        List<ChangeListsHelper.SimpleIntPair> actList = new SoftList<>(originalList);

        //When
        changeForEachListsSame(Arrays.asList(expList, actList), list -> {
            Iterator<ChangeListsHelper.SimpleIntPair> it = list.iterator();
            while (it.hasNext()) {
                ChangeListsHelper.SimpleIntPair item = it.next();
                if (item.a >= 2)
                    it.remove();
            }
            return false;
        });

        //Then
        assertListsSame(expList, actList, new ChangeListsHelper.SimpleIntPair[0]);
    }

    @Test(timeout = 100)
    public void removeByListIterator() {
        //Given
        List<ChangeListsHelper.SimpleIntPair> originalList = Arrays.asList(
                new ChangeListsHelper.SimpleIntPair(1, 2),
                new ChangeListsHelper.SimpleIntPair(1, 3),
                new ChangeListsHelper.SimpleIntPair(1, 4),
                new ChangeListsHelper.SimpleIntPair(1, 5),
                new ChangeListsHelper.SimpleIntPair(2, 5),
                new ChangeListsHelper.SimpleIntPair(3, 5),
                new ChangeListsHelper.SimpleIntPair(4, 5)
        );
        List<ChangeListsHelper.SimpleIntPair> expList = new LinkedList<>(originalList);
        List<ChangeListsHelper.SimpleIntPair> actList = new SoftList<>(originalList);

        //When
        changeForEachListsSame(Arrays.asList(expList, actList), list -> {
            Iterator<ChangeListsHelper.SimpleIntPair> it = list.listIterator();
            while (it.hasNext()) {
                ChangeListsHelper.SimpleIntPair item = it.next();
                if (item.a >= 2)
                    it.remove();
            }
            return false;
        });

        //Then
        assertListsSame(expList, actList, new ChangeListsHelper.SimpleIntPair[0]);
    }


    @Test(timeout = 100)
    public void addByListIterator() {
        //Given
        List<ChangeListsHelper.SimpleIntPair> originalList = Arrays.asList(
                new ChangeListsHelper.SimpleIntPair(1, 2),
                new ChangeListsHelper.SimpleIntPair(1, 3)
        );
        List<ChangeListsHelper.SimpleIntPair> expList = new LinkedList<>(originalList);
        List<ChangeListsHelper.SimpleIntPair> actList = new SoftList<>(originalList);

        //When
        changeForEachListsSame(Arrays.asList(expList, actList), list -> {
            ListIterator<ChangeListsHelper.SimpleIntPair> it = list.listIterator();

            it.add(new ChangeListsHelper.SimpleIntPair(5, 5));
            return null;
        });

        //Then
        assertListsSame(expList, actList, new ChangeListsHelper.SimpleIntPair[0]);
    }


    @Test(timeout = 100)
    public void setByListIterator() {
        //Given
        List<ChangeListsHelper.SimpleIntPair> originalList = Arrays.asList(
                new ChangeListsHelper.SimpleIntPair(1, 2),
                new ChangeListsHelper.SimpleIntPair(1, 3)
        );
        List<ChangeListsHelper.SimpleIntPair> expList = new LinkedList<>(originalList);
        List<ChangeListsHelper.SimpleIntPair> actList = new SoftList<>(originalList);

        //When
        changeForEachListsSame(Arrays.asList(expList, actList), list -> {
            ListIterator<ChangeListsHelper.SimpleIntPair> it = list.listIterator();
            it.next();
            it.set(new ChangeListsHelper.SimpleIntPair(5, 5));
            return null;
        });

        //Then
        assertListsSame(expList, actList, new ChangeListsHelper.SimpleIntPair[0]);
    }

}
