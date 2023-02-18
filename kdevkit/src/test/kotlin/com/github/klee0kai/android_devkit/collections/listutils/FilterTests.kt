package com.github.klee0kai.android_devkit.collections.listutils

import com.github.klee0kai.android_devkit.collections.removeDoubles
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterTests {

    @Test(timeout = 100)
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