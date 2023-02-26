package com.github.klee0kai.hummus.collections.listutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.klee0kai.hummus.collections.ListUtils;
import com.github.klee0kai.hummus.collections.gen.GroupsFlat;
import com.github.klee0kai.hummus.collections.gen.Joins;
import com.github.klee0kai.hummus.collections.gen.Maps;

import org.junit.Test;

import java.util.List;

public class NullsTests {

    @Test(timeout = 100)
    public void filter_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.filter(list, (i, it) -> true));
    }

    @Test(timeout = 100)
    public void first_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.first(list, (i, it) -> true));
    }

    @Test(timeout = 100)
    public void index_null() {
        //When
        List<Integer> list = null;

        //Then
        assertEquals(
                -1,
                ListUtils.index(list, (i, it) -> true)
        );
    }


    @Test(timeout = 100)
    public void contains_null() {
        //When
        List<Integer> list = null;

        //Then
        assertFalse(ListUtils.contains(list, (i, it) -> true));
    }


    @Test(timeout = 100)
    public void remove_doubles_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.removeDoubles(list, (i, it) -> true));
    }

    @Test(timeout = 100)
    public void map_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.map(list, Maps.simple((it) -> true)));
    }

    @Test(timeout = 100)
    public void isEmpty_null() {
        //When
        List<Integer> list = null;

        //Then
        assertTrue(ListUtils.isEmpty(list));
    }


    @Test(timeout = 100)
    public void group_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.group(list, GroupsFlat.simple((it) -> it)));
    }


    @Test(timeout = 100)
    public void leftjoin_null() {
        //When
        List<Integer> list = null;
        List<Integer> list2 = null;

        //Then
        assertNull(
                ListUtils.leftJoin(
                        list,
                        list2,
                        Joins.simplePair((it1, it2) -> true))
        );
    }


    @Test(timeout = 100)
    public void innerjoin_null() {
        //When
        List<Integer> list = null;
        List<Integer> list2 = null;

        //Then
        assertNull(
                ListUtils.innerJoin(
                        list,
                        list2,
                        false,
                        Joins.simpleLeft((it1, it2) -> true))
        );
    }


}
