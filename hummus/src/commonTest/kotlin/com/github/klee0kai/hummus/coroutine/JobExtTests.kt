@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.runTest
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class JobExtTests {

    @Test
    fun emptyJob_should_be_completed() {
        // Given
        val job = emptyJob()

        // When & Then
        assertTrue(job.isCompleted)
        assertFalse(job.isActive)
        assertFalse(job.isCancelled)
    }

    @Test
    fun emptyJob_can_be_awaited() = runTest {
        // Given
        val job = emptyJob()

        // When
        job.join()  // Should not block

        // Then
        assertTrue(job.isCompleted)
    }

    @Test
    fun completeAsync_returns_value() = runTest {
        // Given
        val expected = "test value"

        // When
        val deferred = completeAsync(expected)
        val result = deferred.await()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun completeAsync_is_immediately_resolved() = runTest {
        // Given
        val data = listOf(1, 2, 3)

        // When
        val deferred = completeAsync(data)

        // Then
        assertTrue(deferred.isCompleted)
        assertEquals(data, deferred.await())
    }

    @Test
    fun minDuration_executes_block() = runTest {
        // Given
        var executed = false

        // When
        minDuration(10.milliseconds) {
            executed = true
            "result"
        }

        // Then
        assertTrue(executed)
    }

    @Test
    fun minDuration_returns_block_result() = runTest {
        // Given
        val expectedResult = 42

        // When
        val result = minDuration(10.milliseconds) {
            expectedResult
        }

        // Then
        assertEquals(expectedResult, result)
    }
//
//    @Test
//    fun minDuration_takes_at_least_specified_duration() = runTest {
//        // Given
//        val minDurationMs = 50L
//        val startTime = Clock.System.now().toEpochMilliseconds()
//
//        // When
//        minDuration(minDurationMs.milliseconds) {
//            // Fast operation
//            "done"
//        }
//
//        // Then
//        val elapsedMs = Clock.System.now().toEpochMilliseconds() - startTime
//        assertTrue(elapsedMs >= minDurationMs - 10)  // Allow 10ms margin
//    }
//
//    @Test
//    fun minDuration_does_not_delay_long_operations() = runTest {
//        // Given
//        val startTime = Clock.System.now().toEpochMilliseconds()
//
//        // When
//        minDuration(10.milliseconds) {
//            kotlinx.coroutines.delay(100)  // Simulate long operation
//            "done"
//        }
//
//        // Then
//        val elapsedMs = Clock.System.now().toEpochMilliseconds() - startTime
//        assertTrue(elapsedMs >= 100)  // Should take at least the operation time
//        assertTrue(elapsedMs < 200)   // Allow some margin
//    }

    @Test
    fun awaitSec_returns_value_immediately() = runTest {
        // Given
        val value = "quick result"
        val deferred = completeAsync(value)

        // When
        val result = deferred.awaitSec()

        // Then
        assertEquals(value, result)
    }

    @Test
    fun awaitSec_returns_null_on_timeout() = runTest {
        // Given
        val deferred = async {
            delay(2000)  // Longer than 1 second timeout
            "delayed"
        }

        // When
        val result = deferred.awaitSec()

        // Then
        assertNull(result)
    }

    @Test
    fun awaitSec_completes_for_immediate_result() = runTest {
        // Given
        val deferred = completeAsync(123)

        // When
        val result = deferred.awaitSec()

        // Then
        assertEquals(123, result)
    }
}
