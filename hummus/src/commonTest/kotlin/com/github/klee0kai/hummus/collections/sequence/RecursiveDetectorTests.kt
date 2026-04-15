package com.github.klee0kai.hummus.collections.sequence

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecursiveDetectorTests {

    @Test
    fun detect_no_cycle_in_simple_sequence() {
        val sequence = sequenceOf(1, 2, 3, 4)

        assertFalse(sequence.detectRecursive())
    }

    @Test
    fun detect_cycle_after_full_repeat() {
        val sequence = sequence {
            repeat(10) {
                yield(1)
                yield(2)
                yield(3)
                yield(1)
            }
        }

        assertTrue(sequence.detectRecursive())
    }

    @Test
    fun detect_cycle_with_data_objects() {
        val sequence = sequence {
            repeat(10) {
                yield(TestData(1))
                yield(TestData(2))
                yield(TestData(3))
                yield(TestData(5))
                yield(5)
            }
        }

        assertTrue(sequence.detectRecursive())
    }

    private data class TestData(val value: Int)
}
