package com.github.klee0kai.android_devkit.collections.listutils;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.android_devkit.collections.ListUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class FlatListTests {

    @Test(timeout = 100)
    public void flat_list() {
        //Given
        List<List<Integer>> listOfLists = new LinkedList<>();
        listOfLists.add(Arrays.asList(1, 2, 3));
        listOfLists.add(Arrays.asList(2, 3, 4));
        listOfLists.add(Collections.singletonList(-1));
        listOfLists.add(Collections.emptyList());
        listOfLists.add(null);

        //When
        List<Integer> flatList = ListUtils.flatList(listOfLists);

        //Then
        assertEquals(
                Arrays.asList(1, 2, 3, 2, 3, 4, -1),
                flatList
        );

    }
}
