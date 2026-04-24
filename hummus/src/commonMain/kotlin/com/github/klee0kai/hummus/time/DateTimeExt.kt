package com.github.klee0kai.hummus.time

import kotlinx.datetime.LocalDateTime

/**
 * Formats a LocalDateTime to a standardized string representation.
 *
 * Converts [LocalDateTime] to ISO-8601-like format: `yyyy-MM-dd HH:mm:ss`
 * All components are zero-padded to fixed widths for consistent output.
 *
 * **Format breakdown:**
 * - `yyyy`: 4-digit year (e.g., 2024)
 * - `MM`: 2-digit month (01-12)
 * - `dd`: 2-digit day of month (01-31)
 * - `HH`: 2-digit hour (00-23)
 * - `mm`: 2-digit minute (00-59)
 * - `ss`: 2-digit second (00-59)
 *
 * **Output examples:**
 * ```kotlin
 * LocalDateTime(2024, 3, 15, 9, 5, 3).formatToString()
 * // "2024-03-15 09:05:03"
 *
 * LocalDateTime(2025, 12, 31, 23, 59, 59).formatToString()
 * // "2025-12-31 23:59:59"
 *
 * LocalDateTime(2020, 1, 1, 0, 0, 1).formatToString()
 * // "2020-01-01 00:00:01"
 * ```
 *
 * **Use cases:**
 * - Human-readable date/time in logs
 * - Consistent formatting for display
 * - File naming with timestamps
 * - Database storage with standard format
 * - Debugging and testing
 *
 * **Note:**
 * - Does not include milliseconds or nanoseconds
 * - Does not include timezone information
 * - Space-separated date and time parts for readability
 *
 * @return formatted string in `yyyy-MM-dd HH:mm:ss` format
 */
fun LocalDateTime.formatToString(): String {
    val yyyy = year.toString().padStart(4, '0')
    val mm = monthNumber.toString().padStart(2, '0')
    val dd = dayOfMonth.toString().padStart(2, '0')

    val hh = hour.toString().padStart(2, '0')
    val min = minute.toString().padStart(2, '0')
    val sec = second.toString().padStart(2, '0')

    return "$yyyy-$mm-$dd $hh:$min:$sec"
}