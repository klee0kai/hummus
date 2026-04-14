package com.github.klee0kai.hummus.extensions

import kotlin.math.pow

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
 * Formats a number as a string with a specific decimal exponent precision.
 *
 * Creates a string representation with proper decimal places based on the exponent.
 * Handles integer and decimal parts with appropriate formatting.
 *
 * **Examples:**
 * ```kotlin
 * 1234.56.strFormat(1)   // "123" (10^1 precision)
 * 1234.56.strFormat(-1)  // "1234.5" (0.1 precision)
 * 1234.56.strFormat(-2)  // "1234.56" (0.01 precision)
 * 0.001.strFormat(-3)    // "0.001" (0.001 precision)
 * ```
 *
 * **Behavior:**
 * - exp = 0 or positive: returns only integer part (no decimals)
 * - exp < 0: returns integer.decimal formatted with proper padding
 * - Automatically adds leading zeros after decimal point as needed
 *
 * @param exp the decimal exponent for precision
 * @return formatted string representation
 */
fun Number.strFormat(exp: Int): String {
    if (exp >= 0)
        return this.toInt().toString()

    val multiplier = 10f.pow(-exp).toInt()
    val intValue = this.toFloat()
    val scaled = (intValue * multiplier).toLong()

    return buildString {
        append((scaled / multiplier).toInt())
        append('.')
        val remainder = scaled % multiplier
        val padding = -exp - 1
        for (i in 0 until padding) {
            if (remainder < 10f.pow(i + 1)) {
                append('0')
            }
        }
        append(remainder)
    }
}
