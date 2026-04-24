package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.runTest
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadWriteMutexTests {

    @Test
    fun readLock_allows_concurrent_readers() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        var reader1Done = false
        var reader2Done = false

        // When
        launch {
            mutex.withReadLock {
                reader1Done = true
                kotlinx.coroutines.delay(50)
            }
        }

        launch {
            kotlinx.coroutines.delay(10)
            mutex.withReadLock {
                reader2Done = true
            }
        }

        kotlinx.coroutines.delay(100)

        // Then - both readers should complete
        assertTrue(reader1Done)
        assertTrue(reader2Done)
    }

    @Test
    fun writeLock_blocks_readers() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        var writerDone = false
        var readerDone = false
        var readerStartedBeforeWriterDone = false

        // When
        launch {
            mutex.withWriteLock {
                writerDone = true
                kotlinx.coroutines.delay(50)
                writerDone = false
            }
        }

        launch {
            kotlinx.coroutines.delay(10)
            mutex.withReadLock {
                readerStartedBeforeWriterDone = writerDone
                readerDone = true
            }
        }

        kotlinx.coroutines.delay(150)

        // Then - reader should wait for writer
        assertTrue(readerDone)
        assertFalse(readerStartedBeforeWriterDone)
    }

    @Test
    fun writeLock_blocks_other_writers() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        val results = mutableListOf<String>()

        // When
        launch {
            mutex.withWriteLock {
                results.add("writer1 start")
                kotlinx.coroutines.delay(50)
                results.add("writer1 end")
            }
        }

        launch {
            kotlinx.coroutines.delay(10)
            mutex.withWriteLock {
                results.add("writer2 start")
                results.add("writer2 end")
            }
        }

        kotlinx.coroutines.delay(150)

        // Then - writers should be sequential
        assertEquals(
            listOf(
                "writer1 start",
                "writer1 end",
                "writer2 start",
                "writer2 end"
            ),
            results
        )
    }

    @Test
    fun state_reflects_lock_mode() = runTest {
        // Given
        val mutex = ReadWriteMutex()

        // Initially unlocked
        assertEquals(MutexMode.READ, mutex.state.mode)
        assertEquals(MutexState.UNLOCKED, mutex.state.state)

        // When acquiring read lock
        launch {
            mutex.withReadLock {
                assertEquals(MutexMode.READ, mutex.state.mode)
                assertEquals(MutexState.LOCKED, mutex.state.state)
            }
        }

        kotlinx.coroutines.delay(100)

        // Then - should be unlocked again
        assertEquals(MutexState.UNLOCKED, mutex.state.state)
    }

    @Test
    fun subscribe_notifies_on_state_change() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        val stateChanges = mutableListOf<MutexInfo>()

        mutex.subscribe { info ->
            stateChanges.add(info)
        }

        // When
        launch {
            mutex.withReadLock {
                kotlinx.coroutines.delay(30)
            }
        }

        kotlinx.coroutines.delay(100)

        // Then - should have recorded state changes
        assertTrue(stateChanges.isNotEmpty())
        // First change: READ LOCKED
        assertEquals(MutexMode.READ, stateChanges[0].mode)
        assertEquals(MutexState.LOCKED, stateChanges[0].state)
    }

    @Test
    fun unsubscribe_stops_notifications() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        val notifications = mutableListOf<MutexInfo>()

        val listener: suspend (MutexInfo) -> Unit = { info ->
            notifications.add(info)
        }

        mutex.subscribe(listener)
        mutex.unsubscribe(listener)

        // When
        mutex.withReadLock {
            // Do nothing
        }

        // Then - should not have received notification
        assertEquals(0, notifications.size)
    }

    @Test
    fun writeLock_exception_releases_lock() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        var writerFailed = false
        var readerCompleted = false

        // When
        launch {
            try {
                mutex.withWriteLock {
                    writerFailed = true
                    throw RuntimeException("Write failed")
                }
            } catch (e: RuntimeException) {
                // Expected
            }
        }

        launch {
            kotlinx.coroutines.delay(50)
            mutex.withReadLock {
                readerCompleted = true
            }
        }

        kotlinx.coroutines.delay(150)

        // Then - reader should complete after writer exception
        assertTrue(writerFailed)
        assertTrue(readerCompleted)
    }

    @Test
    fun readLock_exception_releases_lock() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        var readerFailed = false
        var writerCompleted = false

        // When
        launch {
            try {
                mutex.withReadLock {
                    readerFailed = true
                    throw RuntimeException("Read failed")
                }
            } catch (e: RuntimeException) {
                // Expected
            }
        }

        launch {
            kotlinx.coroutines.delay(50)
            mutex.withWriteLock {
                writerCompleted = true
            }
        }

        kotlinx.coroutines.delay(150)

        // Then - writer should complete after reader exception
        assertTrue(readerFailed)
        assertTrue(writerCompleted)
    }

    @Test
    fun stateFlow_emits_state_changes() = runTest {
        // Given
        val mutex = ReadWriteMutex()
        val states = mutableListOf<MutexInfo>()

        val collectJob = launch {
            try {
                mutex.stateFlow().collect { info ->
                    states.add(info)
                }
            } catch (e: Exception) {
                // Expected when job is cancelled
            }
        }

        // When
        mutex.withReadLock {
            kotlinx.coroutines.delay(10)
        }

        kotlinx.coroutines.delay(50)
        collectJob.cancel()
        kotlinx.coroutines.delay(10)

        // Then - should have states
        assertTrue(states.isNotEmpty())
    }
}
