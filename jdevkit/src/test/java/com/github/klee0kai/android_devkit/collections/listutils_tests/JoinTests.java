package com.github.klee0kai.android_devkit.collections.listutils_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.util.Pair;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.gen.Joins;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class JoinTests {

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


    @Test(timeout = 100)
    public void fullouter_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(-1, 2, -3);
        List<Integer> rightNumberCollection = Arrays.asList(2, 3, 6, 4, -3);

        //When
        List<Pair<Integer, Integer>> joined = ListUtils.fullOuterJoin(
                leftNumberCollection,
                rightNumberCollection,
                false,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(6, joined.size());
        assertEquals(new Pair<>(2, 2), joined.get(0));
        assertEquals(new Pair<>(-3, 3), joined.get(1));
        assertEquals(-1, joined.get(2).first.intValue());
        assertNull(joined.get(2).second);
        assertEquals(6, joined.get(3).second.intValue());
        assertNull(joined.get(3).first);
        assertEquals(4, joined.get(4).second.intValue());
        assertNull(joined.get(4).first);
        assertEquals(-3, joined.get(5).second.intValue());
        assertNull(joined.get(5).first);
    }


    @Test(timeout = 100)
    public void fullouter_multi_join() {
        //Given
        List<Integer> leftNumberCollection = Arrays.asList(-1, 2, -3);
        List<Integer> rightNumberCollection = Arrays.asList(2, 3, 6, 4, -3);

        //When
        List<Pair<Integer, Integer>> joined = ListUtils.fullOuterJoin(
                leftNumberCollection,
                rightNumberCollection,
                true,
                Joins.simplePair((((it1, it2) -> Math.abs(it1) == Math.abs(it2))))
        );

        //then
        assertEquals(6, joined.size());
        assertEquals(new Pair<>(2, 2), joined.get(0));
        assertEquals(new Pair<>(-3, 3), joined.get(1));
        assertEquals(new Pair<>(-3, -3), joined.get(2));
        assertEquals(-1, joined.get(3).first.intValue());
        assertNull(joined.get(3).second);
        assertEquals(6, joined.get(4).second.intValue());
        assertNull(joined.get(4).first);
        assertEquals(4, joined.get(5).second.intValue());
        assertNull(joined.get(5).first);
    }

}
