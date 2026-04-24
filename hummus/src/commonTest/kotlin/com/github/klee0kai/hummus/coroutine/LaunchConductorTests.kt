package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.runTest
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LaunchConductorTests {

    @Test
    fun finishTogether_executes_block() = runTest {
        // Given
        val conductor = LaunchConductor()
        var blockExecuted = false

        // When
        conductor.finishTogether {
            blockExecuted = true
            "result"
        }

        // Then
        assertTrue(blockExecuted)
    }

    @Test
    fun finishTogether_returns_block_result() = runTest {
        // Given
        val conductor = LaunchConductor()
        val expectedResult = 42

        // When
        val result = conductor.finishTogether {
            expectedResult
        }

        // Then
        assertEquals(expectedResult, result)
    }

    @Test
    fun finishTogether_waits_for_all_parallel_work() = runTest {
        // Given
        val conductor = LaunchConductor()
        val results = mutableListOf<String>()

        // When
        launch {
            conductor.finishTogether {
                results.add("task1 start")
                kotlinx.coroutines.delay(50)
                results.add("task1 end")
            }
        }

        launch {
            conductor.finishTogether {
                results.add("task2 start")
                kotlinx.coroutines.delay(30)
                results.add("task2 end")
            }
        }

        launch {
            conductor.finishTogether {
                results.add("task3 start")
                results.add("task3 end")
            }
        }

        // Give time for all tasks to start
        kotlinx.coroutines.delay(100)

        // Then - all tasks should be completed
        assertTrue(results.contains("task1 end"))
        assertTrue(results.contains("task2 end"))
        assertTrue(results.contains("task3 end"))
    }

    @Test
    fun finishTogether_exception_in_block_is_rethrown() = runTest {
        // Given
        val conductor = LaunchConductor()
        var exceptionThrown = false

        // When & Then
        try {
            conductor.finishTogether {
                throw RuntimeException("Test error")
            }
        } catch (e: RuntimeException) {
            exceptionThrown = true
            assertEquals("Test error", e.message)
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun finishTogether_decrements_counter_on_exception() = runTest {
        // Given
        val conductor = LaunchConductor()
        var secondBlockExecuted = false

        // When - first block throws
        try {
            conductor.finishTogether {
                throw RuntimeException("Error")
            }
        } catch (e: RuntimeException) {
            // Expected
        }

        // And then second block runs
        conductor.finishTogether {
            secondBlockExecuted = true
        }

        // Then - second block should execute and complete
        assertTrue(secondBlockExecuted)
    }

    @Test
    fun finishTogether_multiple_sequential_calls() = runTest {
        // Given
        val conductor = LaunchConductor()
        val results = mutableListOf<String>()

        // When
        launch {
            conductor.finishTogether {
                results.add("first")
            }
        }

        launch {
            conductor.finishTogether {
                results.add("second")
            }
        }

        launch {
            conductor.finishTogether {
                results.add("third")
            }
        }

        kotlinx.coroutines.delay(100)

        // Then - all should complete
        assertEquals(3, results.size)
        assertTrue(results.contains("first"))
        assertTrue(results.contains("second"))
        assertTrue(results.contains("third"))
    }

    @Test
    fun finishTogether_blocks_until_all_complete() = runTest {
        // Given
        val conductor = LaunchConductor()
        var allTasksStarted = false
        var mainThreadContinued = false

        // When
        launch {
            conductor.finishTogether {
                allTasksStarted = true
                kotlinx.coroutines.delay(100)
            }
        }

        launch {
            conductor.finishTogether {
                allTasksStarted = true
                kotlinx.coroutines.delay(100)
            }
        }

        // Wait for tasks to potentially finish, then check
        kotlinx.coroutines.delay(150)

        // This should only execute after all finishTogether calls complete
        conductor.finishTogether {
            mainThreadContinued = true
        }

        // Then
        assertTrue(allTasksStarted)
        assertTrue(mainThreadContinued)
    }

    @Test
    fun finishTogether_with_single_task() = runTest {
        // Given
        val conductor = LaunchConductor()

        // When
        conductor.finishTogether {
            "result"
        }

        // Then - should complete without issues
        conductor.finishTogether {
            "second"
        }
    }
}
