package com.github.klee0kai.hummus.error

import kotlin.test.*

class ErrorProcessingTests {

    private class CustomException(message: String, cause: Throwable? = null) : Exception(message, cause)
    private class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
    private class TimeoutException(message: String, cause: Throwable? = null) : Exception(message, cause)

    @Test
    fun causes_returns_single_exception_when_no_cause() {
        // Given
        val exception = Exception("root")

        // When
        val causes = exception.causes().toList()

        // Then
        assertEquals(1, causes.size)
        assertEquals(exception, causes[0])
    }

    @Test
    fun causes_returns_all_exceptions_in_chain() {
        // Given
        val rootCause = TimeoutException("timeout")
        val networkError = NetworkException("network failed", rootCause)
        val wrappedException = Exception("operation failed", networkError)

        // When
        val causes = wrappedException.causes().toList()

        // Then
        assertEquals(3, causes.size)
        assertEquals(wrappedException, causes[0])
        assertEquals(networkError, causes[1])
        assertEquals(rootCause, causes[2])
    }

    @Test
    fun causes_is_lazy_sequence() {
        // Given
        val root = Exception("root")
        val middle = Exception("middle", root)
        val top = Exception("top", middle)

        // When
        val sequence = top.causes()

        // Then - sequence should not be materialized yet
        assertTrue(sequence is Sequence<*>)

        // And when we iterate
        val list = sequence.toList()
        assertEquals(3, list.size)
    }

    @Test
    fun cause_finds_exception_by_type() {
        // Given
        val networkError = NetworkException("network failed")
        val wrappedException = Exception("operation failed", networkError)

        // When
        val found = wrappedException.cause<NetworkException>()

        // Then
        assertNotNull(found)
        assertEquals(networkError, found)
    }

    @Test
    fun cause_returns_null_when_type_not_found() {
        // Given
        val exception = Exception("error")

        // When
        val found = exception.cause<NetworkException>()

        // Then
        assertNull(found)
    }

    @Test
    fun cause_finds_first_matching_type() {
        // Given
        val rootCause = NetworkException("root network error")
        val middleCause = CustomException("custom error", rootCause)
        val wrappedException = Exception("top error", middleCause)

        // When
        val found = wrappedException.cause<NetworkException>()

        // Then
        assertNotNull(found)
        assertEquals(rootCause, found)
    }

    @Test
    fun cause_with_kclass_finds_exception() {
        // Given
        val networkError = NetworkException("network failed")
        val wrappedException = Exception("operation failed", networkError)

        // When
        val found = wrappedException.cause(NetworkException::class)

        // Then
        assertNotNull(found)
        assertEquals(networkError, found)
    }

    @Test
    fun cause_with_kclass_returns_null_when_not_found() {
        // Given
        val exception = Exception("error")

        // When
        val found = exception.cause(NetworkException::class)

        // Then
        assertNull(found)
    }

    @Test
    fun isCause_returns_true_when_exception_in_chain() {
        // Given
        val networkError = NetworkException("network failed")
        val wrappedException = Exception("operation failed", networkError)

        // When
        val result = wrappedException.isCause(NetworkException::class)

        // Then
        assertTrue(result)
    }

    @Test
    fun isCause_returns_false_when_exception_not_in_chain() {
        // Given
        val exception = Exception("error")

        // When
        val result = exception.isCause(NetworkException::class)

        // Then
        assertFalse(result)
    }

    @Test
    fun isCause_finds_deep_nested_exception() {
        // Given
        val root = TimeoutException("timeout")
        val middle = NetworkException("network", root)
        val wrapped = Exception("operation", middle)

        // When
        val hasTimeout = wrapped.isCause(TimeoutException::class)
        val hasNetwork = wrapped.isCause(NetworkException::class)
        val hasCustom = wrapped.isCause(CustomException::class)

        // Then
        assertTrue(hasTimeout)
        assertTrue(hasNetwork)
        assertFalse(hasCustom)
    }

    @Test
    fun causes_sequence_can_be_filtered() {
        // Given
        val root = TimeoutException("timeout")
        val middle = NetworkException("network", root)
        val top = Exception("top", middle)

        // When
        val networkExceptions = top.causes()
            .filterIsInstance<NetworkException>()
            .toList()

        // Then
        assertEquals(1, networkExceptions.size)
        assertEquals(middle, networkExceptions[0])
    }

    @Test
    fun causes_sequence_works_with_take() {
        // Given
        val root = Exception("3")
        val middle = Exception("2", root)
        val top = Exception("1", middle)

        // When
        val first2 = top.causes().take(2).toList()

        // Then
        assertEquals(2, first2.size)
        assertEquals(top, first2[0])
        assertEquals(middle, first2[1])
    }
}
