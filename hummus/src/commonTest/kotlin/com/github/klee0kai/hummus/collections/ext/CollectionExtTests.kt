package com.github.klee0kai.hummus.collections.ext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectionExtTests {

    @Test
    fun buildListCount_creates_list_with_count_elements() {
        // Given
        val count = 5

        // When
        val result = buildListCount<String>(count) { "item" }

        // Then
        assertEquals(count, result.size)
    }

    @Test
    fun buildListCount_calls_action_count_times() {
        // Given
        var callCount = 0

        // When
        buildListCount(3) {
            callCount++
            "item"
        }

        // Then
        assertEquals(3, callCount)
    }

    @Test
    fun buildListCount_creates_unique_objects() {
        // Given
        data class Item(val id: Int)

        var counter = 0

        // When
        val result = buildListCount<Item>(3) {
            Item(counter++)
        }

        // Then
        assertEquals(3, result.size)
        assertTrue(result[0] !== result[1])
        assertTrue(result[1] !== result[2])
    }

    @Test
    fun buildListCount_returns_empty_list_for_zero() {
        // Given
        val count = 0

        // When
        val result = buildListCount(count) { "item" }

        // Then
        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

    @Test
    fun buildListCount_with_string_objects() {
        // Given
        val count = 3
        var counter = 0

        // When
        val result = buildListCount<String>(count) {
            "item_${counter++}"
        }

        // Then
        assertEquals(3, result.size)
        assertEquals("item_0", result[0])
        assertEquals("item_1", result[1])
        assertEquals("item_2", result[2])
    }

    @Test
    fun runForEach_executes_action_on_each_item() {
        // Given
        val items = listOf(1, 2, 3)
        var sum = 0

        // When
        items.runForEach {
            sum += this
        }

        // Then
        assertEquals(6, sum)
    }

    @Test
    fun runForEach_with_mutable_objects() {
        // Given
        data class Counter(var count: Int = 0) {
            fun increment() {
                count++
            }
        }

        val counters = listOf(
            Counter(0),
            Counter(0),
            Counter(0)
        )

        // When
        counters.runForEach {
            increment()
            increment()
        }

        // Then
        counters.forEach {
            assertEquals(2, it.count)
        }
    }

    @Test
    fun runForEach_with_side_effects() {
        // Given
        val items = listOf("a", "b", "c")
        val results = mutableListOf<String>()

        // When
        items.runForEach {
            results.add(uppercase())
        }

        // Then
        assertEquals(listOf("A", "B", "C"), results)
    }

    @Test
    fun accumulate_sums_numbers() {
        // Given
        val numbers = listOf(1, 2, 3, 4, 5)

        // When
        val result = numbers.accumulate { a, b -> a + b }

        // Then
        assertEquals(15, result)
    }

    @Test
    fun accumulate_multiplies_numbers() {
        // Given
        val numbers = listOf(2, 3, 4)

        // When
        val result = numbers.accumulate { a, b -> a * b }

        // Then
        assertEquals(24, result)
    }

    @Test
    fun accumulate_concatenates_strings() {
        // Given
        val strings = listOf("a", "b", "c", "d")

        // When
        val result = strings.accumulate { a, b -> "$a-$b" }

        // Then
        assertEquals("a-b-c-d", result)
    }

    @Test
    fun accumulate_with_single_element() {
        // Given
        val items = listOf(42)

        // When
        val result = items.accumulate { a, b -> a + b }

        // Then
        assertEquals(42, result)
    }

    @Test
    fun accumulate_with_empty_list() {
        // Given
        val items = emptyList<Int>()

        // When
        val result = items.accumulate { a, b -> a + b }

        // Then
        assertEquals(null, result)
    }

    @Test
    fun accumulate_complex_objects() {
        // Given
        data class Point(val x: Int, val y: Int) {
            operator fun plus(other: Point) = Point(x + other.x, y + other.y)
        }

        val points = listOf(Point(1, 1), Point(2, 2), Point(3, 3))

        // When
        val result = points.accumulate { a, b -> a + b }

        // Then
        assertEquals(Point(6, 6), result)
    }

    @Test
    fun enumerateAllVariants_single_dimension() {
        // Given
        val variants = listOf("A", "B", "C")

        // When
        val result = enumerateAllVariants(variants).toList()

        // Then
        assertEquals(3, result.size)
        assertEquals(listOf("A"), result[0])
        assertEquals(listOf("B"), result[1])
        assertEquals(listOf("C"), result[2])
    }

    @Test
    fun enumerateAllVariants_two_dimensions() {
        // Given
        val dim1 = listOf("A", "B")
        val dim2 = listOf(1, 2)

        // When
        val result = enumerateAllVariants(dim1, dim2).toList()

        // Then
        assertEquals(4, result.size)
        assertTrue { listOf("A", 1) in result }
        assertTrue { listOf("A", 2) in result }
        assertTrue { listOf("B", 1) in result }
        assertTrue { listOf("B", 2) in result }
    }

    @Test
    fun enumerateAllVariants_three_dimensions() {
        // Given
        val colors = listOf("Red", "Blue")
        val sizes = listOf("S", "M", "L")
        val materials = listOf("Cotton", "Wool")

        // When
        val result = enumerateAllVariants(colors, sizes, materials).toList()

        // Then
        assertEquals(2 * 3 * 2, result.size)
        assertTrue { listOf("Red", "S", "Cotton") in result }
        assertTrue { listOf("Red", "S", "Wool") in result }
        assertTrue { listOf("Red", "M", "Cotton") in result }
    }

    @Test
    fun enumerateAllVariants_empty_dimension() {
        // Given
        val dim1 = listOf("A", "B")
        val dim2 = emptyList<Int>()

        // When
        val result = enumerateAllVariants(dim1, dim2).toList()

        // Then
        assertEquals(0, result.size)
    }

    @Test
    fun enumerateAllVariants_single_element_per_dimension() {
        // Given
        val dim1 = listOf("A")
        val dim2 = listOf(1)
        val dim3 = listOf(true)

        // When
        val result = enumerateAllVariants(dim1, dim2, dim3).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(listOf("A", 1, true), result[0])
    }

    @Test
    fun enumerateAllVariants_is_lazy_sequence() {
        // Given
        val dim1 = listOf("A", "B", "C")
        val dim2 = buildListCount<Int>(100) { 1 }

        // When
        val sequence = enumerateAllVariants(dim1, dim2)

        // Then - should be a sequence
        val first3 = sequence.take(3).toList()
        assertEquals(3, first3.size)
    }

    @Test
    fun enumerateAllVariants_correct_cartesian_product() {
        // Given
        val dim1 = listOf(1, 2)
        val dim2 = listOf("a", "b")

        // When
        val result = enumerateAllVariants(dim1, dim2).toList()

        // Then
        assertTrue { listOf(1, "a") in result }
        assertTrue { listOf(1, "b") in result }
        assertTrue { listOf(2, "a") in result }
        assertTrue { listOf(2, "b") in result }
    }
}
