package com.github.klee0kai.android_devkit.collections.listutils_tests;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.gen.GroupsLists;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GroupToListsTests {

    @Test(timeout = 100)
    public void group_numbers() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, 0, -3, 4, 0, -5, 10, 1);

        //When
        List<List<Integer>> groped = ListUtils.group(
                someNumberCollection,
                GroupsLists.simple(it -> {
                    if (it == 0) {
                        return 0;
                    } else if (it > 0) {
                        return 1;
                    } else return -1;
                }));

        //Then
        assertEquals(
                Arrays.asList(-1, 2, 0, -3, 4, 0, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(3, groped.size());
        assertEquals(
                Arrays.asList(-1, -3, -5),
                groped.get(0)
        );
        assertEquals(
                Arrays.asList(2, 4, 10, 1),
                groped.get(1)
        );
        assertEquals(
                Arrays.asList(0, 0),
                groped.get(2)
        );
    }

}
