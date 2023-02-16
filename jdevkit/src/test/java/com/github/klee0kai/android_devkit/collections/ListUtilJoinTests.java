package com.github.klee0kai.android_devkit.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.util.Pair;

import com.github.klee0kai.android_devkit.collections.gen.Joins;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ListUtilJoinTests {

    @Test(timeout = 100)
    public void left_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(-1, 2, -3);
        List<Integer> rightNumberCollection = Arrays.asList(2, 3, 6, 4, 3);

        //When
        List<Pair<Integer, Integer>> leftJoin = ListUtils.leftJoin(
                leftNumberCollection,
                rightNumberCollection,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(3, leftJoin.size());
        assertEquals(-1, leftJoin.get(0).first.intValue());
        assertNull(leftJoin.get(0).second);
        assertEquals(2, leftJoin.get(1).first.intValue());
        assertEquals(2, leftJoin.get(1).second.intValue());
        assertEquals(-3, leftJoin.get(2).first.intValue());
        assertEquals(3, leftJoin.get(2).second.intValue());
    }


    @Test(timeout = 100)
    public void right_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(2, 3, 6, 4, 3);
        List<Integer> rightNumberCollection = Arrays.asList(-1, 2, -3);

        //When
        List<Pair<Integer, Integer>> leftJoin = ListUtils.rightJoin(
                leftNumberCollection,
                rightNumberCollection,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(3, leftJoin.size());
        assertEquals(-1, leftJoin.get(0).second.intValue());
        assertNull(leftJoin.get(0).first);
        assertEquals(2, leftJoin.get(1).second.intValue());
        assertEquals(2, leftJoin.get(1).first.intValue());
        assertEquals(-3, leftJoin.get(2).second.intValue());
        assertEquals(3, leftJoin.get(2).first.intValue());
    }

    @Test(timeout = 100)
    public void inner_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(-1, 2, -3);
        List<Integer> rightNumberCollection = Arrays.asList(2, 3, 6, 4, -3);

        //When
        List<Pair<Integer, Integer>> leftJoin = ListUtils.innerJoin(
                leftNumberCollection,
                rightNumberCollection,
                false,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(2, leftJoin.size());
        assertEquals(2, leftJoin.get(0).first.intValue());
        assertEquals(2, leftJoin.get(0).second.intValue());
        assertEquals(-3, leftJoin.get(1).first.intValue());
        assertEquals(3, leftJoin.get(1).second.intValue());
    }


    @Test(timeout = 100)
    public void inner_multi_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(-1, 2, -3);
        List<Integer> rightNumberCollection = Arrays.asList(2, 3, 6, 4, -3);

        //When
        List<Pair<Integer, Integer>> leftJoin = ListUtils.innerJoin(
                leftNumberCollection,
                rightNumberCollection,
                true,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(3, leftJoin.size());
        assertEquals(2, leftJoin.get(0).first.intValue());
        assertEquals(2, leftJoin.get(0).second.intValue());
        assertEquals(-3, leftJoin.get(1).first.intValue());
        assertEquals(3, leftJoin.get(1).second.intValue());
        assertEquals(-3, leftJoin.get(2).first.intValue());
        assertEquals(-3, leftJoin.get(2).second.intValue());
    }


}
