package com.github.klee0kai.android_devkit.collections.listutils;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.gen.GroupsFlat;
import com.github.klee0kai.android_devkit.collections.gen.GroupsLists;
import com.github.klee0kai.android_devkit.collections.gen.GroupsToGroups;
import com.github.klee0kai.android_devkit.model.Group;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GroupsTests {

    @Test(timeout = 100)
    public void groupFlatten() {
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

    @Test(timeout = 100)
    public void groupToLists() {
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


    @Test(timeout = 100)
    public void groupToGroups() {
        //Given
        List<Float> someNumberCollection = Arrays.asList(-1f, 2f, 0f, -3f, 4f, 0f, -5f, 10f, 1f);

        //When
        List<Group<Integer, Float>> groped = ListUtils.group(
                someNumberCollection,
                GroupsToGroups.simple(it -> {
                    if (it == 0) {
                        return 0;
                    } else if (it > 0) {
                        return 1;
                    } else return -1;
                }));

        //Then
        assertEquals(
                Arrays.asList(-1f, 2f, 0f, -3f, 4f, 0f, -5f, 10f, 1f),
                someNumberCollection
        );
        assertEquals(3, groped.size());
        assertEquals(
                -1,
                groped.get(0).key.intValue()
        );
        assertEquals(
                Arrays.asList(-1f, -3f, -5f),
                groped.get(0).items
        );
        assertEquals(
                1,
                groped.get(1).key.intValue()
        );
        assertEquals(
                Arrays.asList(2f, 4f, 10f, 1f),
                groped.get(1).items
        );
        assertEquals(
                0,
                groped.get(2).key.intValue()
        );
        assertEquals(
                Arrays.asList(0f, 0f),
                groped.get(2).items
        );
    }


}
