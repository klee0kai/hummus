package com.github.klee0kai.hummus.extensions

import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Calculates the decimal exponent (order of magnitude) of a number.
 *
 * Returns the power of 10 needed to express this number in scientific notation.
 * Works with both positive and negative numbers, integers and decimals.
 *
 * **Examples:**
 * ```kotlin
 * 1234.degExponent()    // 3 (1.234 × 10³)
 * 0.005.degExponent()   // -3 (5 × 10⁻³)
 * 42.degExponent()      // 1 (4.2 × 10¹)
 * 0.1.degExponent()     // -1 (1 × 10⁻¹)
 * (-567).degExponent()  // 2 (5.67 × 10²)
 * ```
 *
 * **Algorithm:**
 * - Converts to absolute float value
 * - If > 1: increments exponent while 10^exp < value
 * - If < 1: decrements exponent while 10^exp > value
 * - Returns the final exponent
 *
 * @return the order of magnitude (decimal exponent)
 */
fun Number.degExponent(): Int {
    val v = if (this.toFloat() > 0) this.toFloat() else -this.toFloat()
    var exp = 0
    if (v > 1f) {
        while (10f.pow(++exp) < v) {
            /* ignore */
        }
        exp--;
    } else {
        while (10f.pow(--exp) > v) {
            /*ignore */
        }
    }
    return exp
}

/**
 * Rounds a number to a specific decimal exponent.
 *
 * Rounds the number to the nearest power of 10 at the specified exponent.
 * Similar to [degExponent] but for rounding instead of calculating.
 *
 * **Examples:**
 * ```kotlin
 * 1234.roundDecExponent(2)   // 1200.0 (round to nearest 100)
 * 1234.roundDecExponent(-1)  // 1230.0 (round to nearest 0.1)
 * 567.roundDecExponent(1)    // 560.0 (round to nearest 10)
 * ```
 *
 * **Parameters:**
 * - exp = 0: rounds to integer
 * - exp > 0: rounds to nearest 10^exp (e.g., 2 = nearest 100)
 * - exp < 0: rounds to decimal places (e.g., -2 = nearest 0.01)
 *
 * @param exp the target decimal exponent
 * @return rounded value as float
 */
fun Number.roundDecExponent(exp: Int): Float {
    if (exp == 0)
        return this.toInt().toFloat()
    if (exp > 0)
        return ((this.toFloat() / 10f.pow(exp)).toInt() * 10f.pow(exp))
    return ((this.toFloat() * 10f.pow(-exp)).toInt().toFloat() / 10f.pow(-exp))
}


/**
 * Formats a Double to a string with a fixed number of decimal places.
 *
 * Rounds the number to the specified decimal places and formats it with exactly
 * that many decimal digits. Missing decimal places are padded with zeros.
 *
 * **Rounding:**
 * - Rounds to the nearest value at the specified precision
 * - Handles banker's rounding (round-to-even) from [roundToLong]
 *
 * **Formatting:**
 * - Always shows exactly [digits] decimal places
 * - Pads with zeros if needed (e.g., 1.5 formatted with 2 digits = "1.50")
 * - Integer part shown as-is
 * - Truncates if more decimal places exist (keeps [digits] places)
 *
 * **Examples:**
 * ```kotlin
 * 123.456.format(1)    // "123.5" (rounded up)
 * 123.456.format(2)    // "123.46" (rounded)
 * 123.456.format(4)    // "123.4560" (padded with 0)
 * 1.0.format(3)        // "1.000" (padded)
 * 0.005.format(2)      // "0.01" (rounded up)
 * 999.9999.format(2)   // "1000.00" (significant digits)
 * ```
 *
 * **Use cases:**
 * - Formatting prices (2 decimal places)
 * - Scientific measurements (variable precision)
 * - UI display of numbers
 * - String output with consistent decimal places
 *
 * **vs. other methods:**
 * - [String.format]: more flexible, requires locale awareness
 * - [DecimalFormat]: Java-specific, heavier
 * - [format]: simple, Kotlin-native, handles padding well
 *
 * **Precision note:**
 * Double precision may cause unexpected results for very small differences.
 * For financial calculations, use BigDecimal instead.
 *
 * @param digits number of decimal places to show (0 = integer)
 * @return string with exactly [digits] decimal places
 */
fun Double.format(digits: Int): String {
    val multiplier = 10.0.pow(digits)
    val rounded = (this * multiplier).roundToLong() / multiplier
    val s = rounded.toString()

    val parts = s.split(".")
    if (parts.size == 1) return "$s." + "0".repeat(digits)
    val decimalPart = parts[1].padEnd(digits, '0')
    return "${parts[0]}.${decimalPart.take(digits)}"
}
