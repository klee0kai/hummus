package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.hummus.collections.weaklist.ChangeListsHelper.assertListsSame
import com.github.klee0kai.hummus.collections.weaklist.ChangeListsHelper.changeForEachListsSame
import kotlin.test.Test

class RefListIteratorTests {

    @Test
    fun removeByIterator() {
        //Given
        val originalList = listOf(
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4),
            Pair(1, 5),
            Pair(2, 5),
            Pair(3, 5),
            Pair(4, 5)
        )
        val expList = mutableListOf(*originalList.toTypedArray())
        val actList = SoftList(originalList) as MutableList<Pair<Int, Int>>

        //When
        changeForEachListsSame(expList, actList) {
            val it = iterator()
            while (it.hasNext()) {
                val item = it.next()
                if (item.first >= 2) it.remove()
            }

        }

        //Then
        assertListsSame(expList, actList)
    }

    @Test
    fun removeByListIterator() {
        //Given
        val originalList = listOf(
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4),
            Pair(1, 5),
            Pair(2, 5),
            Pair(3, 5),
            Pair(4, 5)
        )
        val expList = mutableListOf(*originalList.toTypedArray())
        val actList = SoftList(originalList) as MutableList<Pair<Int, Int>>

        //When
        changeForEachListsSame(expList, actList) {
            val it = listIterator()
            while (it.hasNext()) {
                val item = it.next()
                if (item.first >= 2) it.remove()
            }

        }

        //Then
        assertListsSame(expList, actList)
    }


    @Test()
    fun addByListIterator() {
        //Given
        val originalList = listOf(
            Pair(1, 2),
            Pair(1, 3)
        )
        val expList = mutableListOf(*originalList.toTypedArray())
        val actList = SoftList(originalList) as MutableList<Pair<Int, Int>>

        //When
        changeForEachListsSame(expList, actList) {
            val it = listIterator()
            it.add(Pair(5, 5))
        }

        //Then
        assertListsSame(expList, actList)
    }


    @Test()
    fun setByListIterator() {
        //Given
        val originalList = listOf(
            Pair(1, 2),
            Pair(1, 3)
        )

        val expList = mutableListOf(*originalList.toTypedArray())
        val actList = SoftList(originalList) as MutableList<Pair<Int, Int>>

        //When
        changeForEachListsSame(expList, actList) {
            val it = listIterator()
            it.next()
            it.set(Pair(5, 5))
        }

        //Then
        assertListsSame(expList, actList)
    }


}