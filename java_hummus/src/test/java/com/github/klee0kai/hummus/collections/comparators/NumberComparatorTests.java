package com.github.klee0kai.hummus.collections.comparators;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.hummus.collections.ListUtils;
import com.github.klee0kai.hummus.collections.compare.Comparators;
import com.github.klee0kai.hummus.model.Pair;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NumberComparatorTests {

    @Test(timeout = 100)
    public void number_asc() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, -1, 5, 0, -1);

        //When
        List<Integer> sorted = ListUtils.sort(someNumberCollection, Comparators.byInt(Comparators.SortType.ASC));

        //Then
        assertEquals(
                Arrays.asList(-1, -1, 0, 1, 5),
                sorted
        );
    }


    @Test(timeout = 100)
    public void number_desc() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, -1, 5, 0, -1);

        //When
        List<Integer> sorted = ListUtils.sort(someNumberCollection, Comparators.byInt(Comparators.SortType.DESC));

        //Then
        assertEquals(
                Arrays.asList(5, 1, 0, -1, -1),
                sorted
        );
    }

    @Test(timeout = 100)
    public void number_null() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, null, 5, 0, -1);

        //When
        List<Integer> sorted = ListUtils.sort(someNumberCollection, Comparators.byInt(Comparators.SortType.DESC));

        //Then
        assertEquals(
                Arrays.asList(5, 1, 0, -1, null),
                sorted
        );
    }


    @Test()
    public void number_complex() {
        //Given
        List<Pair<Integer, Integer>> someNumberCollection = Arrays.asList(
                null,
                new Pair<>(1, 2),
                new Pair<>(1, 1),
                new Pair<>(2, 3),
                null,
                new Pair<>(2, null),
                new Pair<>(null, null),
                new Pair<>(null, 4),
                new Pair<>(-1, 4),
                new Pair<>(1, 4)
        );

        //When
        List<Pair<Integer, Integer>> sorted = ListUtils.sort(
                someNumberCollection,
                Comparators.firstNotZero(
                        Comparators.byNotNull(Comparators.SortType.ASC),
                        Comparators.byNotNull(Comparators.SortType.ASC, it -> it != null ? it.first : null),
                        Comparators.byNotNull(Comparators.SortType.ASC, it -> it != null ? it.second : null),
                        Comparators.byInt(Comparators.SortType.ASC, it -> it != null ? it.first : null),
                        Comparators.byInt(Comparators.SortType.DESC, it -> it != null ? it.second : null)
                )
        );

        //Then
        assertEquals(
                Arrays.asList(
                        null,
                        null,
                        new Pair<>(null, null),
                        new Pair<>(null, 4),
                        new Pair<>(2, null),
                        new Pair<>(-1, 4),
                        new Pair<>(1, 4),
                        new Pair<>(1, 2),
                        new Pair<>(1, 1),
                        new Pair<>(2, 3)
                ),
                sorted
        );
    }

}
