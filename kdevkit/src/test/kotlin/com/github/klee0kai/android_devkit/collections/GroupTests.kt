package com.github.klee0kai.android_devkit.collections

import com.github.klee0kai.android_devkit.collections.gen.GroupGrouping
import junit.framework.Assert.assertEquals
import org.junit.Test

class GroupTests {

    @Test(timeout = 100)
    fun groupFlatten() {
        //Given
        val someNumberCollection = listOf(-1, 2, 0, -3, 4, 0, -5, 10, 1)

        //When
        val groped: List<Int> = someNumberCollection.group(
            grouping = GroupGrouping.groupingFlatten(),
            groupId = {
                when {
                    it < 0 -> -1
                    it > 0 -> 1
                    else -> 0
                }
            },
        )

        //Then
        assertEquals(
            listOf(-1, 2, 0, -3, 4, 0, -5, 10, 1),
            someNumberCollection
        )
        assertEquals(
            listOf(-1, -3, -5, 2, 4, 10, 1, 0, 0),
            groped
        )
    }

    @Test(timeout = 100)
    fun groupToLists() {
        //Given
        val someNumberCollection = listOf(-1, 2, 0, -3, 4, 0, -5, 10, 1)

        //When
        val grouped = someNumberCollection.group(
            grouping = GroupGrouping.groupingToLists(),
            groupId = {
                when {
                    it < 0 -> -1
                    it > 0 -> 1
                    else -> 0
                }
            },
        )

        //Then

        //Then
        assertEquals(
            listOf(-1, 2, 0, -3, 4, 0, -5, 10, 1),
            someNumberCollection
        )
        assertEquals(3, grouped.size.toLong())
        assertEquals(
            listOf(-1, -3, -5),
            grouped[0]
        )
        assertEquals(
            listOf(2, 4, 10, 1),
            grouped[1]
        )
        assertEquals(
            listOf(0, 0),
            grouped[2]
        )
    }


    @Test(timeout = 100)
    fun groupToGroups() {
        //Given
        val someNumberCollection = listOf(-1f, 2f, 0f, -3f, 4f, 0f, -5f, 10f, 1f)

        //When
        val grouped = someNumberCollection.group(
            grouping = GroupGrouping.groupingToGroups(),
            groupId = {
                when {
                    it < 0 -> -1
                    it > 0 -> 1
                    else -> 0
                }
            },
        )

        //Then
        assertEquals(
            listOf(-1f, 2f, 0f, -3f, 4f, 0f, -5f, 10f, 1f),
            someNumberCollection
        )
        assertEquals(3, grouped.size.toLong())
        assertEquals(
            -1,
            grouped[0].key
        )
        assertEquals(
            listOf(-1f, -3f, -5f),
            grouped[0].items
        )
        assertEquals(
            1,
            grouped[1].key
        )
        assertEquals(
            listOf(2f, 4f, 10f, 1f),
            grouped[1].items
        )
        assertEquals(
            0,
            grouped[2].key
        )
        assertEquals(
            listOf(0f, 0f),
            grouped[2].items
        )
    }

}