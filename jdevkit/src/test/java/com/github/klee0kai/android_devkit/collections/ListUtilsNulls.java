package com.github.klee0kai.android_devkit.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class ListUtilsNulls {

    @Test
    public void filter_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.filter(list, (i, it) -> true));
    }

    @Test
    public void first_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.first(list, (i, it) -> true));
    }

    @Test
    public void index_null() {
        //When
        List<Integer> list = null;

        //Then
        assertEquals(
                -1,
                ListUtils.index(list, (i, it) -> true)
        );
    }


    @Test
    public void contains_null() {
        //When
        List<Integer> list = null;

        //Then
        assertFalse(ListUtils.contains(list, (i, it) -> true));
    }


    @Test
    public void remove_doubles_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.removeDoubles(list, (i, it) -> true));
    }

    @Test
    public void map_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.map(list, (it) -> true));
    }

    @Test
    public void isEmpty_null() {
        //When
        List<Integer> list = null;

        //Then
        assertTrue(ListUtils.isEmpty(list));
    }


    @Test
    public void group_null() {
        //When
        List<Integer> list = null;

        //Then
        assertNull(ListUtils.group(list, new ListUtils.IGroup<Integer, Integer>() {
            @Override
            public int groupId(Integer it) {
                return 0;
            }

            @Override
            public List<Integer> group(List<Integer> lGroup) {
                return Collections.emptyList();
            }
        }));
    }


    @Test
    public void leftjoin_null() {
        //When
        List<Integer> list = null;
        List<Integer> list2 = null;

        //Then
        assertNull(ListUtils.leftJoin(list, list2, new ListUtils.IJoin<Integer, Integer, Integer>() {
            @Override
            public boolean isJoin(Integer it1, Integer it2) {
                return false;
            }

            @Override
            public Integer join(@Nullable Integer it1, @Nullable Integer it2) {
                return null;
            }
        }));
    }


    @Test
    public void innerjoin_null() {
        //When
        List<Integer> list = null;
        List<Integer> list2 = null;

        //Then
        assertNull(ListUtils.innerJoin(list, list2, false, new ListUtils.IJoin<Integer, Integer, Integer>() {
            @Override
            public boolean isJoin(Integer it1, Integer it2) {
                return false;
            }

            @Override
            public Integer join(@Nullable Integer it1, @Nullable Integer it2) {
                return null;
            }
        }));
    }


}
