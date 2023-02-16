package com.github.klee0kai.android_devkit.collections;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.android_devkit.collections.gen.GroupsFlat;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ListUtilsGroupFlatTests {

    @Test(timeout = 100)
    public void group_numbers() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, 0, -3, 4, 0, -5, 10, 1);

        //When
        List<Integer> groped = ListUtils.group(
                someNumberCollection,
                GroupsFlat.simple(it -> {
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
        assertEquals(
                Arrays.asList(-1, -3, -5, 2, 4, 10, 1, 0, 0),
                groped
        );
    }

}
