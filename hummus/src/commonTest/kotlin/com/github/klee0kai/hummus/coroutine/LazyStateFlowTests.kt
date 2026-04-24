package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.IgnoreJs
import com.github.klee0kai.hummus.IgnoreNative
import com.github.klee0kai.hummus.runTest
import kotlinx.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class LazyStateFlowTests {

    @Test
    fun lazyStateFlow_starts_with_init_value() = runTest {
        // Given
        val scope = CoroutineScope(Job())

        // When
        val flow = lazyStateFlow(
            init = "initial",
            defaultArg = Unit,
            scope = scope,
            block = { }
        )

        // Then
        assertEquals("initial", flow.value)
        scope.cancel()
    }

    @Test
    fun lazyStateFlow_updates_on_first_subscriber() = runTest {
        // Given
        val values = mutableListOf<String>()
        var blockExecuted = false

        val flow = lazyStateFlow(
            init = "initial",
            defaultArg = Unit,
            scope = this,
            block = {
                delay(5.milliseconds)
                blockExecuted = true
                value = "updated"
            }
        )

        // When
        launch {
            flow.collect { value ->
                values.add(value)
                if (values.size >= 2) {
                    this.cancel()
                }
            }
        }

        delay(50.milliseconds)

        // Then
        assertTrue(blockExecuted)
        assertTrue(values.contains("initial"))
        assertTrue(values.contains("updated"))
    }

    @Test
    @IgnoreJs
    @IgnoreNative
    fun lazyStateFlow_touch_reruns_block() = runTest {
        // Given
        var executionCount = 0

        val flow = lazyStateFlow(
            init = 0,
            defaultArg = Unit,
            scope = this,
            block = {
                executionCount++
                value = executionCount
            }
        )

        launch {
            flow.collect { }
        }

        delay(50.milliseconds)

        // When
        flow.touch()
        delay(50.milliseconds)

        // Then
        assertEquals(2, executionCount)
    }

    @Test
    fun lazyStateFlow_touch_with_argument() = runTest {
        // Given
        val arguments = mutableListOf<String>()

        val flow = lazyStateFlow(
            init = "",
            defaultArg = "default",
            scope = this,
            block = { arg ->
                arguments.add(arg)
                value = arg
            }
        )

        launch {
            flow.collect { }
        }

        delay(100.milliseconds)

        // When
        flow.touch("custom_arg")
        delay(100.milliseconds)

        // Then
        assertTrue(arguments.contains("default"))
        assertTrue(arguments.contains("custom_arg"))
    }

    @Test
    fun lazyStateFlow_cancelTouch_cancels_update() = runTest {
        // Given
        val scope = CoroutineScope(Job())
        var executionCount = 0

        val flow = lazyStateFlow(
            init = 0,
            defaultArg = Unit,
            scope = scope,
            block = {
                executionCount++
                delay(100.milliseconds)
                value = executionCount
            }
        )

        launch {
            flow.collect { }
        }

        delay(50.milliseconds)

        // When
        flow.cancelTouch()
        delay(100.milliseconds)

        // Then - the delayed block should be cancelled
        // Note: exact count depends on timing, but it shouldn't complete the update
        assertEquals(0, flow.value)
        scope.cancel()
    }

    @Test
    fun lazyStateFlow_without_subscribers_does_not_update() = runTest {
        // Given
        val scope = CoroutineScope(Job())
        var blockExecuted = false

        val flow = lazyStateFlow(
            init = "initial",
            defaultArg = Unit,
            scope = scope,
            block = {
                blockExecuted = true
                value = "updated"
            }
        )

        // When - don't subscribe, just check value
        delay(100.milliseconds)

        // Then - block should not have executed
        assertFalse(blockExecuted)
        assertEquals("initial", flow.value)
        scope.cancel()
    }

    @Test
    fun lazyStateFlow_can_be_used_as_mutable_state_flow() = runTest {
        // Given
        val scope = CoroutineScope(Job())

        val flow = lazyStateFlow(
            init = 0,
            defaultArg = Unit,
            scope = scope,
            block = { }
        )

        // When
        flow.value = 42
        val result = flow.value

        // Then
        assertEquals(42, result)
        scope.cancel()
    }

    @Test
    @IgnoreJs
    @IgnoreNative
    fun lazyStateFlow_emits_new_values() = runTest {
        // Given
        val emittedValues = mutableListOf<Int>()

        val flow = lazyStateFlow(
            init = 0,
            defaultArg = Unit,
            scope = this,
            block = {
                delay(1.milliseconds)
                emit(1)
                delay(1.milliseconds)
                emit(2)
                delay(1.milliseconds)
                emit(3)
            }
        )

        // When
        launch {
            flow.collect { value ->
                emittedValues.add(value)
                if (emittedValues.size >= 4) {
                    this.cancel()
                }
            }
        }

        delay(100.milliseconds)

        // Then - should have collected all emitted values
        assertTrue(emittedValues.contains(0))  // initial
        assertTrue(emittedValues.contains(1))
        assertTrue(emittedValues.contains(2))
        assertTrue(emittedValues.contains(3))
    }

    @Test
    fun lazyStateFlow_multiple_touch_calls() = runTest {
        // Given
        val scope = CoroutineScope(Job())
        val executionOrder = mutableListOf<String>()

        val flow = lazyStateFlow(
            init = "",
            defaultArg = Unit,
            scope = scope,
            block = {
                executionOrder.add("executed")
                value = "${executionOrder.size}"
            }
        )

        launch {
            flow.collect { }
        }

        delay(50.milliseconds)

        // When
        flow.touch()
        delay(50.milliseconds)
        flow.touch()
        delay(50.milliseconds)

        // Then
        assertTrue(executionOrder.size >= 2, "count ${executionOrder.size}")
        scope.cancel()
    }
}
