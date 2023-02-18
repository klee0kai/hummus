package com.github.klee0kai.android_devkit.collections.listutils;

import static org.junit.Assert.assertEquals;

import com.github.klee0kai.android_devkit.collections.ListUtils;
import com.github.klee0kai.android_devkit.collections.gen.Maps;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MapTests {

    @Test(timeout = 100)
    public void int_to_string() {
        //Given
        List<Integer> someNumberCollection = Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5, 10, 1);

        //When
        List<String> stringNumbers = ListUtils.map(someNumberCollection, Maps.simple(Object::toString));

        //Then
        assertEquals(
                Arrays.asList(1, 2, 3, 4, 56, -1, 2, -3, 4, -5, 10, 1),
                someNumberCollection
        );
        assertEquals(
                Arrays.asList("1", "2", "3", "4", "56", "-1", "2", "-3", "4", "-5", "10", "1"),
                stringNumbers
        );
    }

}
