package com.github.klee0kai.android_devkit.collections

import com.github.klee0kai.android_devkit.collections.gen.Joins
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class JoinTests {

    @Test(timeout = 100)
    fun left_join() {
        //Given
        val leftNumberCollection = listOf(-1, 2, -3)
        val rightNumberCollection = listOf(2, 3, 6, 4, 3)

        //When
        val leftJoin = leftNumberCollection.leftJoin(
            list = rightNumberCollection,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair()
        )


        //then
        assertEquals(3, leftJoin.size)
        assertEquals(-1, leftJoin[0].first)
        assertNull(leftJoin[0].second)
        assertEquals(2, leftJoin[1].first)
        assertEquals(2, leftJoin[1].second)
        assertEquals(-3, leftJoin[2].first)
        assertEquals(3, leftJoin[2].second)
    }

    @Test(timeout = 100)
    fun right_join() {
        //Given
        val leftNumberCollection = listOf(2, 3, 6, 4, 3)
        val rightNumberCollection = listOf(-1, 2, -3)

        //When
        val rightJoin = leftNumberCollection.rightJoin(
            list = rightNumberCollection,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair()
        )

        //then
        assertEquals(3, rightJoin.size)
        assertEquals(-1, rightJoin[0].second)
        assertNull(rightJoin[0].first)
        assertEquals(2, rightJoin[1].second)
        assertEquals(2, rightJoin[1].first)
        assertEquals(-3, rightJoin[2].second)
        assertEquals(3, rightJoin[2].first)
    }

    @Test(timeout = 100)
    fun inner_join() {
        //Given
        val leftNumberCollection = listOf(-1, 2, -3)
        val rightNumberCollection = listOf(2, 3, 6, 4, -3)

        //When
        val innerJoin = leftNumberCollection.innerJoin(
            list = rightNumberCollection,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair()
        )

        //then
        assertEquals(2, innerJoin.size.toLong())
        assertEquals(2, innerJoin[0].first)
        assertEquals(2, innerJoin[0].second)
        assertEquals(-3, innerJoin[1].first)
        assertEquals(3, innerJoin[1].second)
    }

    @Test(timeout = 100)
    fun inner_multi_join() {
        //Given
        val leftNumberCollection = listOf(-1, 2, -3)
        val rightNumberCollection = listOf(2, 3, 6, 4, -3)

        //When
        val innerJoin = leftNumberCollection.innerJoin(
            list = rightNumberCollection,
            multiToMulti = true,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair(),
        )


        //then
        assertEquals(3, innerJoin.size.toLong())
        assertEquals(2, innerJoin[0].first)
        assertEquals(2, innerJoin[0].second)
        assertEquals(-3, innerJoin[1].first)
        assertEquals(3, innerJoin[1].second)
        assertEquals(-3, innerJoin[2].first)
        assertEquals(-3, innerJoin[2].second)
    }

    @Test(timeout = 100)
    fun fullouter_join() {
        //Given
        val leftNumberCollection = listOf(-1, 2, -3)
        val rightNumberCollection = listOf(2, 3, 6, 4, -3)

        //When
        val joined = leftNumberCollection.fullOuterJoin(
            list = rightNumberCollection,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair(),
        )


        //then
        assertEquals(6, joined.size.toLong())
        assertEquals(Pair(2, 2), joined[0])
        assertEquals(Pair(-3, 3), joined[1])
        assertEquals(-1, joined[2].first)
        assertNull(joined[2].second)
        assertEquals(6, joined[3].second)
        assertNull(joined[3].first)
        assertEquals(4, joined[4].second)
        assertNull(joined[4].first)
        assertEquals(-3, joined[5].second)
        assertNull(joined[5].first)
    }

    @Test(timeout = 100)
    fun fullouter_multi_join() {
        //Given
        val leftNumberCollection = listOf(-1, 2, -3)
        val rightNumberCollection = listOf(2, 3, 6, 4, -3)

        //When
        val joined = leftNumberCollection.fullOuterJoin(
            list = rightNumberCollection,
            multiToMulti = true,
            isJoin = { it1, it2 -> abs(it1) == abs(it2) },
            join = Joins.pair(),
        )

        //then
        assertEquals(6, joined.size.toLong())
        assertEquals(Pair(2, 2), joined[0])
        assertEquals(Pair(-3, 3), joined[1])
        assertEquals(Pair(-3, -3), joined[2])
        assertEquals(-1, joined[3].first)
        assertNull(joined[3].second)
        assertEquals(6, joined[4].second)
        assertNull(joined[4].first)
        assertEquals(4, joined[5].second)
        assertNull(joined[5].first)
    }
}