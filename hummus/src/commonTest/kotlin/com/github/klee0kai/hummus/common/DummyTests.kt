package com.github.klee0kai.hummus.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DummyTests {

    @Test
    fun dummyId_increments() {
        // Given
        val id1 = Dummy.dummyId

        // When
        val id2 = Dummy.dummyId

        // Then
        assertTrue(id2 > id1)
        assertEquals(1, id2 - id1)
    }

    @Test
    fun dummyId_is_unique() {
        // Given
        val ids = mutableSetOf<Int>()

        // When
        repeat(100) {
            ids.add(Dummy.dummyId)
        }

        // Then - all 100 IDs should be unique
        assertEquals(100, ids.size)
    }

    @Test
    fun dummyId_is_positive() {
        // Given & When
        val id = Dummy.dummyId

        // Then
        assertTrue(id > 0)
    }

    @Test
    fun unicString_returns_uuid_format() {
        // Given & When
        val str = Dummy.unicString

        // Then
        assertTrue(str.isNotEmpty())
        // UUID format: 8-4-4-4-12 hex digits with dashes
        assertTrue(str.contains("-"))
        assertEquals(36, str.length)  // Standard UUID string length
    }

    @Test
    fun unicString_generates_unique_strings() {
        // Given
        val strings = mutableSetOf<String>()

        // When
        repeat(100) {
            strings.add(Dummy.unicString)
        }

        // Then - all strings should be unique
        assertEquals(100, strings.size)
    }

    @Test
    fun unicString_differs_on_each_call() {
        // Given & When
        val str1 = Dummy.unicString
        val str2 = Dummy.unicString

        // Then
        assertNotEquals(str1, str2)
    }

    @Test
    fun dummyId_vs_unicString() {
        // Given
        val id = Dummy.dummyId
        val str = Dummy.unicString

        // Then - id should be numeric, string should contain hyphens
        assertTrue(id > 0)
        assertTrue(str.contains("-"))
    }
}
