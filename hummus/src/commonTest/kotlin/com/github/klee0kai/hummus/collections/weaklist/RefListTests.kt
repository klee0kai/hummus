package com.github.klee0kai.hummus.collections.weaklist

import com.github.klee0kai.hummus.collections.weaklist.ChangeListsHelper.assertListsSame
import com.github.klee0kai.hummus.collections.weaklist.ChangeListsHelper.changeForEachListsSame
import com.github.klee0kai.stone.weakref.Memory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RefListTests {

    @Test
    fun emptyList() {
        //When
        val exampleList: List<Int?> = mutableListOf()
        val refList: List<Int?> = SoftList()

        //Then
        assertListsSame(exampleList, refList)
    }


    @Test
    fun initList() {
        //When
        val exampleList: List<Int?> = mutableListOf(1, 2, 4)
        val refList: List<Int?> = SoftList(listOf(1, 2, 4))


        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun addOne() {
        //When
        val exampleList: MutableList<Int?> = mutableListOf()
        val refList = SoftList<Int?>()
        changeForEachListsSame(exampleList, refList) {
            add(1)
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun addFew() {
        //When
        val exampleList: MutableList<Int?> = mutableListOf()
        val refList = SoftList<Int?>()
        changeForEachListsSame(exampleList, refList) {
            add(1)
            add(2)
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun addRemove() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            add(Pair(1, 2))
            add(Pair(2, 3))
            add(Pair(3, 4))
            removeAt(2)
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun removeAll() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            removeAll(
                listOf(
                    Pair(1, 3),
                    Pair(1, 4)
                )
            )
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun nothingRemoveAll() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            removeAll(
                listOf(
                    Pair(7, 3),
                    Pair(7, 4)
                )
            )
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun clear() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            clear()
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun retailAll() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            retainAll(
                listOf(
                    Pair(1, 3),
                    Pair(1, 4)
                )
            )
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun nothingRetailAll() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            retainAll(
                listOf(
                    Pair(7, 3),
                    Pair(7, 4)
                )
            )
        }

        //Then
        assertListsSame(exampleList, refList)
    }

    @Test
    fun removeByIndex() {
        //When
        val exampleList: MutableList<Pair<Int, Int>?> = mutableListOf()
        val refList = SoftList<Pair<Int, Int>?>()
        changeForEachListsSame(exampleList, refList) {
            addAll(
                listOf(
                    Pair(1, 2),
                    Pair(1, 3),
                    Pair(1, 4),
                    Pair(1, 5),
                    Pair(2, 5),
                    Pair(3, 5),
                    Pair(4, 5)
                )
            )
            removeAt(3)
        }

        //Then
        assertListsSame(exampleList, refList)
    }


    @Test
    fun sameLists() {
        //When
        val ref1List = SoftList<Pair<Int, Int>>()
        val ref2List = SoftList<Pair<Int, Int>>()
        changeForEachListsSame(ref1List, ref2List) {
            add(Pair(1, 2))
            add(Pair(2, 3))
            add(Pair(3, 4))
        }

        //Then
        assertEquals(ref1List.hashCode(), ref2List.hashCode())
        assertEquals(ref1List, ref2List)
    }

    @Test
    fun diffLists() {
        //When
        val ref1List = SoftList<Pair<Int, Int>>()
        val ref2List = SoftList<Pair<Int, Int>>()
        changeForEachListsSame(ref1List, ref2List) {
            add(Pair(1, 2))
            add(Pair(2, 3))
            add(Pair(3, 4))
        }
        ref2List.add(Pair(3, 4))

        //Then
        assertNotEquals(ref1List.hashCode().toLong(), ref2List.hashCode().toLong())
        assertNotEquals(ref1List, ref2List)
    }


    @Test
    fun weakItemsCollect() {
        //Given
        val strongRef = Pair(7, 4)
        val refList = WeakList<Pair<Int, Int>>()
        refList.add(Pair(1, 2))
        refList.add(Pair(3, 4))
        refList.add(strongRef)
        refList.add(Pair(5, 4))

        //When
        Memory.gc()
        refList.clearNulls()

        //Then
        assertEquals(1, refList.size.toLong())
        assertEquals(strongRef, refList[0])
    }


}