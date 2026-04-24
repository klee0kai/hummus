package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.runTest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class SafeContextScopeTests {

    @Test
    fun launchSafe_executes_block() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        var blockExecuted = false

        // When
        val job = scope.launchSafe {
            blockExecuted = true
        }
        job.join()

        // Then
        assertTrue(blockExecuted)
    }

    @Test
    fun launchSafe_tracks_execution_with_flow() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val trackFlow = MutableStateFlow(0)

        // When
        val job = scope.launchSafe(trackFlow = trackFlow) {
            delay(100.milliseconds)
        }

        delay(1.milliseconds)
        assertTrue(trackFlow.value > 0)
        job.join()

        // Then - should be decremented after completion
        assertEquals(0, trackFlow.value)
    }

    @Test
    fun launchLatest_cancels_previous_job() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val results = mutableListOf<String>()

        // When
        val job1 = scope.launchLatest("key") {
            results.add("job1 start")
            delay(100)
            results.add("job1 end")
        }

        delay(10)

        val job2 = scope.launchLatest("key") {
            results.add("job2 start")
            results.add("job2 end")
        }

        job2.join()
        delay(50)

        // Then - job1 should be cancelled, job2 should run
        assertTrue(results.contains("job2 start"))
        assertTrue(results.contains("job2 end"))
        assertFalse(results.contains("job1 end"))
    }

    @Test
    fun launchIfNotStarted_returns_existing_job() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val key = "test-key"

        // When
        val job1 = scope.launchIfNotStarted(key) {
            delay(100)
        }

        val job2 = scope.launchIfNotStarted(key) {
            // This should not execute
            throw AssertionError("Second job should not run")
        }

        // Then
        assertEquals(job1, job2)
        job1.join()
    }

    @Test
    fun launchIfNotStarted_launches_new_job_after_completion() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val key = "test-key"
        var executionCount = 0

        // When
        val job1 = scope.launchIfNotStarted(key) {
            executionCount++
        }
        job1.join()

        val job2 = scope.launchIfNotStarted(key) {
            executionCount++
        }
        job2.join()

        // Then - should have launched twice (different jobs)
        assertEquals(2, executionCount)
    }

    @Test
    fun asyncSafe_returns_result() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())

        // When
        val deferred = scope.asyncSafe {
            42
        }

        val result = deferred.await()

        // Then
        assertEquals(42, result)
    }

    @Test
    fun asyncSafe_exception_propagates() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        var exceptionCaught = false

        // When & Then
        try {
            val deferred = scope.asyncSafe {
                throw RuntimeException("Test error")
            }
            deferred.await()
        } catch (e: RuntimeException) {
            exceptionCaught = true
        }

        assertTrue(exceptionCaught)
    }

    @Test
    fun asyncResult_wraps_exception_in_result() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())

        // When
        val deferred = scope.asyncResult {
            throw RuntimeException("Test error")
        }

        val result = deferred.await()

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is RuntimeException)
    }

    @Test
    fun asyncResult_successful_returns_success() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())

        // When
        val deferred = scope.asyncResult {
            "success"
        }

        val result = deferred.await()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("success", result.getOrNull())
    }

    @Test
    fun launchSafe_mutex_provides_synchronization() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        var counter = 0

        // When - launch concurrent safe operations
        val job1 = scope.launchSafe {
            counter++
            delay(10)
            counter++
        }

        val job2 = scope.launchSafe {
            delay(5)
            counter++
            delay(10)
            counter++
        }

        job1.join()
        job2.join()

        // Then - should be executed safely (counter = 4)
        assertEquals(4, counter)
    }

    @Test
    fun launchLatestSafe_combines_latest_and_safe() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val results = mutableListOf<String>()

        // When
        scope.launchLatestSafe("key") {
            results.add("first")
            delay(50)
            results.add("first end")
        }

        delay(10)

        scope.launchLatestSafe("key") {
            results.add("second")
            results.add("second end")
        }

        delay(100)

        // Then
        assertTrue(results.contains("second"))
        assertTrue(results.contains("second end"))
        assertFalse(results.contains("first end"))
    }

    @Test
    fun withLockOrRun_executes_with_null_mutex() = runTest {
        // Given
        val mutex: kotlinx.coroutines.sync.Mutex? = null
        var executed = false

        // When
        mutex.withLockOrRun {
            executed = true
            42
        }

        // Then
        assertTrue(executed)
    }

    @Test
    fun withLockOrRun_executes_with_mutex() = runTest {
        // Given
        val mutex = kotlinx.coroutines.sync.Mutex()
        var executed = false

        // When
        mutex.withLockOrRun {
            executed = true
            42
        }

        // Then
        assertTrue(executed)
    }

    @Test
    fun trackFlow_counts_concurrent_operations() = runTest {
        // Given
        val scope = SafeContextScope(coroutineContext + Job())
        val trackFlow = MutableStateFlow(0)

        // When
        scope.launch(trackFlow = trackFlow) {
            delay(50)
        }

        scope.launch(trackFlow = trackFlow) {
            delay(50)
        }

        delay(10)

        // Then - both jobs should be tracked
        assertEquals(2, trackFlow.value)
    }
}
