package com.github.klee0kai.hummus.collections.listutils

import com.github.klee0kai.hummus.collections.removeDoubles
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTests {

    @Test
    fun remove_doubles() {
        //Given
        val someNumberCollection = listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, -1)

        //When
        val unicNumbers = someNumberCollection.removeDoubles()

        //Then
        assertEquals(
            listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, -1),
            someNumberCollection
        )
        assertEquals(
            listOf(1, 2, 3, -1),
            unicNumbers
        )
    }

}