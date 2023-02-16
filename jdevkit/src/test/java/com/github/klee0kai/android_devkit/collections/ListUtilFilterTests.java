package com.github.klee0kai.android_devkit.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.klee0kai.android_devkit.collections.gen.Filters;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListUtilFilterTests {

    @Test
    public void middle_filter() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5, 10, 1);

        //When
        List<Integer> positiveNumbers = ListUtils.filter(someNumberCollection, (i, it) -> {
            return it >= 0;
        });

        //Then
        assertEquals(
                Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList(1, 2, 3, 4, 56, 2, 4, 10, 1),
                positiveNumbers
        );
    }


    @Test
    public void end_filter() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5);

        //When
        List<Integer> positiveNumbers = ListUtils.filter(someNumberCollection, (i, it) -> {
            return it >= 0;
        });

        //Then
        assertEquals(
                Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList(1, 2, 3, 4, 56, 2, 4),
                positiveNumbers
        );
    }


    @Test
    public void start_filter() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        List<Integer> positiveNumbers = ListUtils.filter(someNumberCollection, Filters.simple((it) -> {
            return it >= 0;
        }));

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList(2, 4, 10, 1),
                positiveNumbers
        );
    }


    @Test
    public void filter_by_index() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        List<Integer> first3Numbers = ListUtils.filter(someNumberCollection, Filters.sublist(0, 2));

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList(-1, 2, -3),
                first3Numbers
        );
    }


    @Test
    public void first_find() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        Integer positiveNumber = ListUtils.first(someNumberCollection, (i, it) -> {
            return it >= 0;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                2,
                positiveNumber.intValue()
        );
    }


    @Test
    public void first_in_first() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        Integer negativeNumber = ListUtils.first(someNumberCollection, (i, it) -> {
            return it < 0;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                -1,
                negativeNumber.intValue()
        );
    }


    @Test
    public void first_no_found() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        Integer negativeNumber = ListUtils.first(someNumberCollection, (i, it) -> {
            return it > 1000;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertNull(
                negativeNumber
        );
    }


    @Test
    public void index_first() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        int negativeNumberIndex = ListUtils.index(someNumberCollection, (i, it) -> {
            return it < 0;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                0,
                negativeNumberIndex
        );
    }


    @Test
    public void index_second() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        int positiveNumberIndex = ListUtils.index(someNumberCollection, (i, it) -> {
            return it >= 0;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                1,
                positiveNumberIndex
        );
    }

    @Test
    public void contains_in_list() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        boolean positiveNumberContains = ListUtils.contains(someNumberCollection, (i, it) -> {
            return it >= 0;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertTrue(positiveNumberContains);
    }


    @Test
    public void non_contains_in_list() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(-1, 2, -3, 4, -5, 10, 1);

        //When
        boolean bigNumbersContains = ListUtils.contains(someNumberCollection, (i, it) -> {
            return it >= 1000;
        });

        //Then
        assertEquals(
                Arrays.asList(-1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertFalse(bigNumbersContains);
    }


    @Test
    public void remove_doubles() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3, -1);

        //When
        List<Integer> unicNumbers = ListUtils.removeDoubles(someNumberCollection, Objects::equals);

        //Then
        assertEquals(
                Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3, -1),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList(1, 2, 3, -1),
                unicNumbers
        );
    }


}
