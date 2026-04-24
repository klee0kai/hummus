package com.github.klee0kai.hummus.common

import com.github.klee0kai.hummus.cleanable.Cleanable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CleanableTests {

    private class TestResource : Cleanable {
        var isClean = false
        val data = mutableListOf<String>()

        override fun clean() {
            isClean = true
            data.clear()
        }
    }

    private class DefaultCleanable : Cleanable {
        var cleanCalled = false

        // Override to track if default clean is called
        override fun clean() {
            cleanCalled = true
            super.clean()
        }
    }

    @Test
    fun cleanable_clean_can_be_implemented() {
        // Given
        val resource = TestResource()
        resource.data.add("test")

        // When
        resource.clean()

        // Then
        assertTrue(resource.isClean)
        assertTrue(resource.data.isEmpty())
    }

    @Test
    fun cleanable_clean_is_idempotent() {
        // Given
        val resource = TestResource()
        resource.data.add("test")

        // When
        resource.clean()
        resource.clean()
        resource.clean()

        // Then - multiple calls should be safe
        assertTrue(resource.isClean)
        assertTrue(resource.data.isEmpty())
    }

    @Test
    fun cleanable_default_implementation_does_nothing() {
        // Given
        val cleanable = DefaultCleanable()

        // When
        cleanable.clean()

        // Then - default implementation should execute without error
        assertTrue(cleanable.cleanCalled)
    }

    @Test
    fun cleanable_multiple_objects_batch_cleanup() {
        // Given
        val resource1 = TestResource()
        val resource2 = TestResource()
        val resource3 = TestResource()

        resource1.data.add("data1")
        resource2.data.add("data2")
        resource3.data.add("data3")

        // When
        listOf(resource1, resource2, resource3).forEach { it.clean() }

        // Then - all should be cleaned
        assertTrue(resource1.isClean)
        assertTrue(resource2.isClean)
        assertTrue(resource3.isClean)
    }

    @Test
    fun cleanable_in_list() {
        // Given
        val resources = listOf(
            TestResource(),
            TestResource(),
            TestResource()
        )

        resources.forEach { it.data.add("item") }

        // When
        val cleaned = resources.map { it.apply { clean() } }

        // Then
        cleaned.forEach { resource ->
            assertTrue(resource.isClean)
            assertTrue(resource.data.isEmpty())
        }
    }

    @Test
    fun cleanable_cleanup_order() {
        // Given
        val cleanupOrder = mutableListOf<String>()

        class OrderedResource(val name: String) : Cleanable {
            override fun clean() {
                cleanupOrder.add(name)
            }
        }

        val resources = listOf(
            OrderedResource("first"),
            OrderedResource("second"),
            OrderedResource("third")
        )

        // When
        resources.forEach { it.clean() }

        // Then - order should be preserved
        assertEquals(listOf("first", "second", "third"), cleanupOrder)
    }

    @Test
    fun cleanable_exception_in_clean() {
        // Given
        class FailingResource : Cleanable {
            override fun clean() {
                throw RuntimeException("Clean failed")
            }
        }

        val resource = FailingResource()

        // When & Then - exception should propagate
        try {
            resource.clean()
            assertFalse(true)  // Should not reach here
        } catch (e: RuntimeException) {
            assertEquals("Clean failed", e.message)
        }
    }

    @Test
    fun cleanable_interface_on_complex_object() {
        // Given
        class DataCache : Cleanable {
            private val cache = mutableMapOf<String, String>()
            private val listeners = mutableListOf<() -> Unit>()

            fun put(key: String, value: String) {
                cache[key] = value
            }

            fun subscribe(listener: () -> Unit) {
                listeners.add(listener)
            }

            override fun clean() {
                cache.clear()
                listeners.clear()
            }

            fun isCleaned(): Boolean = cache.isEmpty() && listeners.isEmpty()
        }

        val cache = DataCache()
        cache.put("key1", "value1")
        cache.put("key2", "value2")
        cache.subscribe { }

        // When
        cache.clean()

        // Then
        assertTrue(cache.isCleaned())
    }
}
