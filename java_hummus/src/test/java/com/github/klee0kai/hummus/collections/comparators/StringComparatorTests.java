package com.github.klee0kai.hummus.collections.comparators;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.hummus.collections.ListUtils;
import com.github.klee0kai.hummus.collections.compare.Comparators;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StringComparatorTests {

    @Test()
    public void string_asc() {
        //Given
        List<String> strCollection = Arrays.asList("c", "a", null, "d", "b");

        //When
        List<String> sorted = ListUtils.sort(strCollection, Comparators.byString(Comparators.SortType.ASC));

        //Then
        assertEquals(
                Arrays.asList(null, "a", "b", "c", "d"),
                sorted
        );
    }


    @Test()
    public void string_desc() {
        //Given
        List<String> strCollection = Arrays.asList("c", "a", null, "d", "b");

        //When
        List<String> sorted = ListUtils.sort(strCollection, Comparators.byString(Comparators.SortType.DESC));

        //Then
        assertEquals(
                Arrays.asList("d", "c", "b", "a", null),
                sorted
        );
    }


}
