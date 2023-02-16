package com.github.klee0kai.android_devkit.collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class ListUtilsEmptyTests {

    @Test
    public void null_is_empty() {
        //When
        List<Integer> null_list = null;

        //Then
        assertTrue(ListUtils.isEmpty(null_list));
    }

    @Test
    public void empty_is_empty() {
        //When
        List<Integer> null_list = Collections.emptyList();

        //Then
        assertTrue(ListUtils.isEmpty(null_list));
    }


    @Test
    public void nonempty_is_nonempty() {
        //When
        List<Integer> null_list = Collections.singletonList(1);

        //Then
        assertFalse(ListUtils.isEmpty(null_list));
    }


}
